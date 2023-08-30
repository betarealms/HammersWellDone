package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Tool;
import com.betarealms.hammerswelldone.types.Type;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This is a collection of utilities for working with Tools.
 */
public class ToolManager {

  private ToolManager() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Encodes Type and Tier into a single int using bitwise operations.
   * We use lower 4 bits for Type and next 4 bits for Tier.
   */
  public static int encodeCustomModelData(Type type, Tier tier) {
    return ((tier.getBit() << 4) | type.getBit()) + 11200100;
  }

  /**
   * Decodes the Type from the given encoded int.
   */
  public static Type decodeType(int customModelData) {
    return Type.values()[(customModelData - 11200100) & 0xF];  // Lower 4 bits
  }

  /**
   * Decodes the Tier from the given encoded int.
   */
  public static Tier decodeTier(int customModelData) {
    return Tier.values()[((customModelData - 11200100) >> 4) & 0xF];  // Next 4 bits
  }

  /**
   * Creates an ItemStack from the given Material and Tool.
   */
  public static ItemStack getItemStack(Material material, Tool tool) {
    ItemStack result = new ItemStack(material, 1);

    // Set meta
    ItemMeta meta = result.getItemMeta();
    assert meta != null;
    meta.setCustomModelData(tool.getCustomModelData());
    meta.setDisplayName(ChatColor.RESET + tool.getDisplayName(material));
    meta.setLore(tool.getLore());
    result.setItemMeta(meta);

    return result;

  }

  /*
   * Returns the Tool based on the provided Material and Tier as an ItemStack.
   */
  public static ItemStack getItemStack(Material material, Type type, Tier tier) {
    return getItemStack(material, Objects.requireNonNull(Tool.getTool(type, tier)));
  }

  /**
   * Check whether a tool is a Tier 2 or Tier 3 tool.
   */
  public static boolean isCustomTool(ItemStack item) {
    if (item.getItemMeta() == null) {
      return false;
    }
    return isCustomTool(item.getItemMeta());
  }

  /**
   * Check whether a tool is a Tier 2 or Tier 3 tool.
   */
  public static boolean isCustomTool(ItemMeta meta) {
    return (meta.hasCustomModelData()
        && (ToolManager.decodeTier(meta.getCustomModelData()) == Tier.ADVANCED
        || ToolManager.decodeTier(meta.getCustomModelData()) == Tier.GOD));
  }
}
