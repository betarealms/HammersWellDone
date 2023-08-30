package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Type;
import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
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
        if (canBreakBlock(block, itemInHand)) {
          block.breakNaturally(itemInHand);
        }
        int damageModifier = 1;
        // Get tool level and add damageModifier
        switch (ToolManager.decodeTier(meta.getCustomModelData())) {
          case ADVANCED -> damageModifier += 2; // Given the amount of resources: +200% durability
          case GOD -> damageModifier += 9; // Given the amount of resources: +900% durability
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
        // Calculate chance for damaging
        if (r <= (100) / (damageModifier)) {
          // Deal damage to the item
          Damageable itemDamageable = (Damageable) meta;
          itemDamageable.setDamage(itemDamageable.getDamage() + 1);
          itemInHand.setItemMeta(itemDamageable);
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

  private static boolean canBreakBlock(Block block, ItemStack itemInHand) {
    return block.isPreferredTool(itemInHand);
  }
}
