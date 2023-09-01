package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Type;
import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
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
  // Using a HashMap to store the BlockFace for each player.
  private final ConcurrentHashMap<UUID, BlockFace> playerBlockFaceMap = new ConcurrentHashMap<>();

  // This is used to keep track whether to stay in the block breaking loop.
  private final ConcurrentHashMap<UUID, Integer> playerBreakingMap = new ConcurrentHashMap<>();

  /**
   * This triggers on player , for ex. when punching.
   *
   * @param event PlayerInteractEvent
   */
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    // Store the BlockFace when player starts to interact with a block
    BlockFace face = event.getBlockFace();
    UUID playerUnique = event.getPlayer().getUniqueId();
    playerBlockFaceMap.put(playerUnique, face);

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
      if (bestType != currentType) {
        // Get current material
        String currentMaterialName = itemInHand.getType().name().split("_")[0];

        // Get new material
        Material targetMaterial = Material.getMaterial(currentMaterialName + "_" + bestType.name());

        // Set target material
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
      if (!event.isCancelled()) {
        // Check if it can be broken
        if (canBreakBlock(player, block)) {
          block.breakNaturally(itemInHand);
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
      }
    } else {
      // Get mined blocks face
      final BlockFace blockFace = playerBlockFaceMap.get(player.getUniqueId());

      // Get surrounding blocks
      final List<Block> surroundingBlocks = BlockManager.getSurroundingBlocks(
          ToolManager.decodeTier(meta.getCustomModelData()).getBit(), block, blockFace);

      // Add the mined blocks to playerBreakingMap
      playerBreakingMap.put(player.getUniqueId(), surroundingBlocks.size());

      // Iterate through all the blocks to remove
      for (Block blockToRemove : surroundingBlocks) {
        // Start a new instance of BlockBreakEvent that will break the block
        BlockBreakEvent eventBlockBreak = new BlockBreakEvent(blockToRemove, player);
        Bukkit.getPluginManager().callEvent(eventBlockBreak);
      }
    }

    // Remove BlockFace as it's not needed anymore
    playerBlockFaceMap.remove(player.getUniqueId());
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

    // Test whether there is a tool that drops more than one item
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

    try {
      for (HashMap.Entry<Type, Material> entry : new HashMap<>(typeMap).entrySet()) {
        // Create a new testing tool and add it to player's hand
        ItemStack newItem = new ItemStack(entry.getValue());
        player.getInventory().setItemInMainHand(newItem);

        // Get breakSpeed
        float breakSpeed = block.getBreakSpeed(player);

        // Is it smaller than or equal to the bestBreakSpeed?
        if (breakSpeed <= bestBreakSpeed) {
          typeMap.remove(entry.getKey());
        } else {
          bestBreakSpeed = breakSpeed;
        }
      }
    } finally {
      // Return the original player's tool
      player.getInventory().setItemInMainHand(itemInHand);
    }

    // Return the best tool. If there's multiple left, they should be equal
    return typeMap.keySet().iterator().next();
  }

  private static boolean canBreakBlock(Player player, Block block) {
    // Check whether block is unbreakable
    float breakSpeed = block.getBreakSpeed(player);
    if (Math.signum(breakSpeed) == 0) {
      return false;
    }

    ItemStack itemInHand = player.getInventory().getItemInMainHand();
    ItemMeta meta = itemInHand.getItemMeta();

    // Check whether player's tool is the best tool if not SUPER
    if (meta != null && ToolManager.decodeType(meta.getCustomModelData()) != Type.SUPER) {
      Type type = ToolManager.decodeType(meta.getCustomModelData());
      Type bestType = getBestType(player, block);

      return type == bestType;
    }

    return true;
  }
}
