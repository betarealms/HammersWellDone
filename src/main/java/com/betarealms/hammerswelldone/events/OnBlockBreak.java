package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Tool;
import com.betarealms.hammerswelldone.types.Type;
import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class handles block breaking.
 */
public class OnBlockBreak implements Listener {
  // This is used to keep track whether to stay in the block breaking loop.
  private final ConcurrentHashMap<UUID, Integer> playerBreakingMap = new ConcurrentHashMap<>();

  // This is used to keep track of experience during block breaking
  private final ConcurrentHashMap<UUID, Integer> playerExpMap = new ConcurrentHashMap<>();

  /**
   * This triggers on player , for ex. when punching.
   *
   * @param event PlayerInteractEvent
   */
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Get player and block
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if (block == null) {
      return;
    }

    // SUPER handling
    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    ItemMeta meta = itemInHand.getItemMeta();
    if (meta == null) {
      return;
    }

    // Is it a custom tool and a SUPER?
    if (ToolManager.isCustomTool(meta)
        && ToolManager.decodeType(itemInHand.getItemMeta().getCustomModelData()) == Type.SUPER) {
      // Check what is the best tool to mine the block with
      Type bestType = getBestType(player, block);
      Type currentType = ToolManager.decodeType(itemInHand.getItemMeta().getCustomModelData());
      // Switch to that tool
      if (bestType != null && bestType != currentType) {
        // Get current material
        String currentMaterialName = itemInHand.getType().name().split("_")[0];

        // Get new material
        Material targetMaterial = Material.getMaterial(currentMaterialName + "_" + bestType.name());

        // Set target material
        assert targetMaterial != null;
        itemInHand.setType(targetMaterial);
        player.getInventory().setItemInMainHand(itemInHand);
      }
    }
  }

  /**
   * This triggers on block break.
   *
   * @param event BlockBreakEvent
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBreak(BlockBreakEvent event) {
    // Get player who broke the block
    Player player = event.getPlayer();

    // Get item in player's hand
    ItemStack itemInHand = player.getInventory().getItemInMainHand();

    // Null checks and Material type check for optimization
    if (itemInHand.getType() == Material.AIR || !itemInHand.hasItemMeta()) {
      return;
    }

    // Get item meta
    ItemMeta meta = itemInHand.getItemMeta();

    // Prevent NullPointerException
    if (meta == null) {
      return;
    }

    // Check if tool isn't from this plugin
    if (!ToolManager.isCustomTool(meta)) {
      return;
    }

    // Get mined block
    final Block block = event.getBlock();

    // Is this in a recursive loop?
    // Does playerBreakingMap contain a key for this player?
    // Is the block being broken not the same as the mined block?
    if (playerBreakingMap.containsKey(player.getUniqueId())
        && playerBreakingMap.get(player.getUniqueId()) > 0) {
      // Remove 1 from the playerBreakingMap value
      playerBreakingMap.put(player.getUniqueId(), playerBreakingMap.get(player.getUniqueId()) - 1);
      // Check if event is cancelled
      if (event.isCancelled()) {
        return;
      }
      // Check if it can be broken
      if (!canBreakBlock(player, block)) {
        return;
      }

      // Add experience to drop later
      playerExpMap.put(player.getUniqueId(),
          playerExpMap.get(player.getUniqueId())
          + calculateXpToDrop(block.getType()));

      // Break the block
      block.breakNaturally(itemInHand);

      // Damage the item
      damageTool(player, itemInHand, event);
    } else {
      // Get blockFace
      BlockFace blockFace = getBlockFace(player);

      if (blockFace == null) {
        player.sendMessage(ChatColor.RED + "BlockFace is NULL, please report this bug.");
        return;
      }

      // Get surrounding blocks
      final List<Block> surroundingBlocks = ToolManager.decodeTier(meta.getCustomModelData())
          == Tier.VANILLA
          ? List.of(block)
          : BlockManager.getSurroundingBlocks(
          ToolManager.decodeTier(meta.getCustomModelData()).getBit(), block, blockFace);

      // Add the mined blocks to playerBreakingMap
      playerBreakingMap.put(player.getUniqueId(), surroundingBlocks.size());

      // Create a new entry in playerExpMap
      playerExpMap.put(player.getUniqueId(), 0);

      // Iterate through all the blocks to remove
      for (Block blockToRemove : surroundingBlocks) {
        // Start a new instance of BlockBreakEvent that will break the block
        BlockBreakEvent eventBlockBreak = new BlockBreakEvent(blockToRemove, player);
        Bukkit.getPluginManager().callEvent(eventBlockBreak);
      }

      // Drop XP
      int xpToDrop = playerExpMap.get(player.getUniqueId());
      if (xpToDrop > 0) {
        block.getWorld().spawn(block.getLocation(), ExperienceOrb.class).setExperience(xpToDrop);
      }
    }
  }

  private static Type getBestType(Player player, Block block) {
    // Create a HashMap of all the types
    HashMap<Type, Material> typeMap = new HashMap<>();
    typeMap.put(Type.PICKAXE, Material.NETHERITE_PICKAXE);
    typeMap.put(Type.SHOVEL, Material.NETHERITE_SHOVEL);
    typeMap.put(Type.AXE, Material.NETHERITE_AXE);
    typeMap.put(Type.HOE, Material.NETHERITE_HOE);

    // Test for preferred tool
    for (HashMap.Entry<Type, Material> entry : new HashMap<>(typeMap).entrySet()) {
      if (!block.isPreferredTool(new ItemStack(entry.getValue()))) {
        typeMap.remove(entry.getKey());
      }
    }

    // Check whether only one type is left
    if (typeMap.size() == 1) {
      return typeMap.keySet().iterator().next();
    }

    // Create a backup in case target block drops nothing
    HashMap<Type, Material> backupMap = new HashMap<>(typeMap);

    // Test whether there is a tool that drops no items
    for (HashMap.Entry<Type, Material> entry : new HashMap<>(typeMap).entrySet()) {
      if (block.getDrops(new ItemStack(entry.getValue())).isEmpty()) {
        typeMap.remove(entry.getKey());
      }
    }

    // Check whether all tools were removed
    if (typeMap.isEmpty()) {
      typeMap.putAll(backupMap);
    } else if (typeMap.size() == 1) { // Else check whether there is only one type
      return typeMap.keySet().iterator().next();
    }

    // Get player's item in hand
    final ItemStack itemInHand = player.getInventory().getItemInMainHand();

    // Test for tool breaking speed
    float bestBreakSpeed = 0.0f;
    Type bestType = null;

    try {
      for (HashMap.Entry<Type, Material> entry : new HashMap<>(typeMap).entrySet()) {
        // Create a new testing tool and add it to player's hand
        ItemStack newItem = new ItemStack(entry.getValue());
        player.getInventory().setItemInMainHand(newItem);

        // Get breakSpeed
        float breakSpeed = block.getBreakSpeed(player);

        // Is it bigger than the bestBreakSpeed?
        if (breakSpeed > bestBreakSpeed) {
          bestBreakSpeed = breakSpeed;
          bestType = entry.getKey();
        }
      }
    } finally {
      // Return the original player's tool
      player.getInventory().setItemInMainHand(itemInHand);
    }

    // Return the best tool
    return bestType;
  }

  private static boolean canBreakBlock(Player player, Block block) {
    // Check whether block is unbreakable
    float breakSpeed = block.getBreakSpeed(player);
    if (Math.signum(breakSpeed) == 0) {
      return false;
    }

    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    ItemMeta meta = itemInHand.getItemMeta();

    // Null pointer check
    if (meta == null) {
      return false;
    }

    // Check whether the player's tool is the best tool if not SUPER
    if (ToolManager.decodeType(meta.getCustomModelData()) != Type.SUPER) {
      Type type = ToolManager.decodeType(meta.getCustomModelData());
      Type bestType = getBestType(player, block);

      if (type != bestType) {
        return false;
      }
    }

    // Check whether the player's tool's material is good enough by using drops comparison
    if (block.getDrops(itemInHand).isEmpty()) {
      // Iterate through materialNames
      for (String materialName : Tool.getMaterialNames().keySet()) {
        // Get material
        Material material = Material.getMaterial(
            materialName + "_" + meta.getDisplayName().split("_")[1]);
        // Check if there's any drops
        assert material != null;
        if (!block.getDrops(new ItemStack(material)).isEmpty()) {
          return false;
        }
      }
    }

    return true;
  }

  private void damageTool(Player player, ItemStack itemInHand, BlockBreakEvent event) {
    // Get item meta
    ItemMeta meta = itemInHand.getItemMeta();
    assert meta != null;

    int damageModifier = 1;
    // Get tool level and add damageModifier
    switch (ToolManager.decodeTier(meta.getCustomModelData())) {
      case ADVANCED -> damageModifier += 2; // Given the amount of resources: +200% durability
      case GOD -> damageModifier += 9; // Given the amount of resources: +900% durability
      default -> { }
    }
    // Add more damageModifier for SUPER
    if (ToolManager.decodeType(meta.getCustomModelData()) == Type.SUPER) {
      damageModifier *= 4; // It is composed of four tools
      damageModifier += 3; // Account for additional materials used to craft the SUPER
    }
    // Account for enchantments
    if (meta.hasEnchant(Enchantment.DURABILITY)) {
      damageModifier += meta.getEnchantLevel(Enchantment.DURABILITY);
    }
    // Get random
    Random rand = new Random();
    int r = rand.nextInt(100) + 1;
    Damageable itemDamageable = (Damageable) meta;
    // Calculate chance for damaging
    if (r <= (100) / (damageModifier)) {
      // Deal damage to the item
      itemDamageable.setDamage(itemDamageable.getDamage() + 1);
      itemInHand.setItemMeta(itemDamageable);
      player.getInventory().setItemInMainHand(itemInHand);
    }
    // Is the item broken?
    if (itemDamageable.getDamage() >= itemInHand.getType().getMaxDurability()) {
      // Remove the item from hand
      player.getInventory().setItemInMainHand(null);
      // Play breaking sound
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
      // Stop the event
      event.setCancelled(true);
      playerBreakingMap.put(player.getUniqueId(), 0);
    }
  }

  /**
   * Get Block Face of the block player is looking at.
   * Big thanks to <a href="https://www.spigotmc.org/threads/getting-the-blockface-of-a-targeted-block.319181/">Benz56</a> for this beautiful solution.
   *
   * @param player player
   * @return Block Face
   */
  private BlockFace getBlockFace(Player player) {
    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) {
      return null;
    }
    Block targetBlock = lastTwoTargetBlocks.get(1);
    Block adjacentBlock = lastTwoTargetBlocks.get(0);
    return targetBlock.getFace(adjacentBlock);
  }

  /**
   * This is used as a workaround to calculate XP drops on block breaking.
   *
   * @param material block material
   * @return amount of xp
   */
  private int calculateXpToDrop(Material material) {
    Random rand = new Random();
    return switch (material) {
      case COAL_ORE -> rand.nextInt(3);
      case NETHER_GOLD_ORE -> rand.nextInt(2);
      case DIAMOND_ORE, EMERALD_ORE -> rand.nextInt((7 - 3) + 1) + 3;
      case LAPIS_ORE, NETHER_QUARTZ_ORE -> rand.nextInt((5 - 2) + 1) + 2;
      case REDSTONE_ORE -> rand.nextInt((5 - 1) + 1) + 1;
      case SPAWNER -> rand.nextInt((43 - 15) + 1) + 15;
      case SCULK -> 1;
      case SCULK_SENSOR, SCULK_SHRIEKER, SCULK_CATALYST -> 5;
      default -> 0;
    };
  }
}
