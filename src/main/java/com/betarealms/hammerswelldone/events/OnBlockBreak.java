package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.utils.BlockManager;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.HashMap;
import java.util.UUID;
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
  // Using a HashMap to store the BlockFace for each player
  private final HashMap<UUID, BlockFace> playerBlockFaceMap = new HashMap<>();

  /**
   * This triggers on player interact.
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

    ItemMeta meta = itemInHand.getItemMeta();

    // Prevent NullPointerException
    if (meta == null) {
      return;
    }

    // Check for customModelData
    if (meta.hasCustomModelData() && (ToolManager.decodeTier(meta.getCustomModelData())
        == Tier.ADVANCED || ToolManager.decodeTier(meta.getCustomModelData()) == Tier.GOD)) {

      // Get mined block
      final Block block = event.getBlock();

      // Get mined blocks face
      final BlockFace blockFace = playerBlockFaceMap.getOrDefault(player.getUniqueId(), null);

      for (Block b : BlockManager.getSurroundingBlocks(
          ToolManager.decodeTier(meta.getCustomModelData()).getBit(), block, blockFace)) {
        b.breakNaturally();
      }

      // Remove mined block
      playerBlockFaceMap.remove(player.getUniqueId());

    }
  }
}
