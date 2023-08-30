package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
        // Todo: DAMAGE
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
        BlockBreakEvent e = new BlockBreakEvent(blockToRemove, player);
        Bukkit.getPluginManager().callEvent(e);
      }
    }

    // Remove BlockFace as it's not needed anymore
    playerBlockFaceMap.remove(player.getUniqueId());
  }

  private static boolean canBreakBlock(Block block, ItemStack itemInHand) {
    return block.isPreferredTool(itemInHand);
  }
}
