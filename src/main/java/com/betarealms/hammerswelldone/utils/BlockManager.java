package com.betarealms.hammerswelldone.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class is used to manage blocks.
 */
public class BlockManager {

  private BlockManager() {
    // This constructor is intentionally empty. Nothing to initialize.
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * This function returns a list of blocks surrounding the given block.
   *
   * @param radius Radius from the center block, e.g. 0 = 1x1, 1 = 3x3 ...
   * @param block Center block
   * @param blockFace BlockFace that the player is mining
   * @return List of blocks
   */
  public static List<Block> getSurroundingBlocks(
      int radius,
      @NonNull Block block,
      @NonNull BlockFace blockFace) {
    // Pre-calculate the size of the List for better performance
    final List<Block> blocks = new ArrayList<>((2 * radius + 1) * (2 * radius + 1));

    // Get target Block
    World world = block.getWorld();
    final int x = block.getX();
    final int y = block.getY()
        // Adjust the Y level of the central block in order to keep it centered
        + ((blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) ? 0 : radius - 1);
    final int z = block.getZ();

    // Calculate start and end block
    int startX = x - radius;
    int endX = x + radius;
    int startY = y - radius;
    int endY = y + radius;
    int startZ = z - radius;
    int endZ = z + radius;

    // Adjust according to the BlockFace
    switch (blockFace) {
      case UP:
      case DOWN:
        startY = endY = y;
        break;
      case EAST:
      case WEST:
        startX = endX = x;
        break;
      case NORTH:
      case SOUTH:
        startZ = endZ = z;
        break;
      default:
        break;
    }

    // Loop and populate blocks list
    for (int i = startX; i <= endX; i++) {
      for (int j = startY; j <= endY; j++) {
        for (int k = startZ; k <= endZ; k++) {
          blocks.add(world.getBlockAt(i, j, k));
        }
      }
    }

    // Remove null Blocks from the List
    blocks.removeAll(Collections.singleton(null));

    return blocks;
  }
}
