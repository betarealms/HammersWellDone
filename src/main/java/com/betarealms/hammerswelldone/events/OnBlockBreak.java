package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class handles block breaking.
 */
public class OnBlockBreak implements Listener {
  // Using a HashMap to store the BlockFace for each player.
  private final HashMap<UUID, BlockFace> playerBlockFaceMap = new HashMap<>();

  // This is used to keep track whether to stay in the block breaking loop.
  private final HashMap<UUID, Block> playerBreakingMap = new HashMap<>();

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
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
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
    if (!meta.hasCustomModelData()
        || (ToolManager.decodeTier(meta.getCustomModelData()) != Tier.ADVANCED
        && ToolManager.decodeTier(meta.getCustomModelData()) != Tier.GOD)) {
      return;
    }

    // Get mined block
    final Block block = event.getBlock();

    // Is this in a recursive loop?
    // Does playerBreakingMap contain a key for this player?
    // Is the block being broken the same as the mined block?
    if (playerBreakingMap.containsKey(player.getUniqueId())
        && !block.equals(playerBreakingMap.get(player.getUniqueId()))) {
      block.breakNaturally(itemInHand);
      // Todo: DAMAGE
    } else {
      // Add the mined block to playerBreakingMap
      playerBreakingMap.put(player.getUniqueId(), block);

      // Get mined blocks face
      final BlockFace blockFace = playerBlockFaceMap.getOrDefault(player.getUniqueId(), null);

      // Iterate through all the blocks to remove
      for (Block blockToRemove : BlockManager.getSurroundingBlocks(
          ToolManager.decodeTier(meta.getCustomModelData()).getBit(), block, blockFace)) {
        // Check if it can be broken
        if (canBreakBlock(blockToRemove, itemInHand)) {
          // Start a new instance of BlockBreakEvent that will break the block
          BlockBreakEvent e = new BlockBreakEvent(blockToRemove, player);
          Bukkit.getPluginManager().callEvent(e);
        }
      }

      // Remove the mined block from playerBreakingMap
      playerBreakingMap.remove(player.getUniqueId());
    }

    // Remove BlockFace as it's not needed anymore
    playerBlockFaceMap.remove(player.getUniqueId());
  }

  private static boolean canBreakBlock(Block block, ItemStack itemInHand) {
    return block.isPreferredTool(itemInHand);
  }
}
