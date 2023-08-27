package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Tool;
import com.betarealms.hammerswelldone.types.Type;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    return Type.values()[customModelData & 0xF];  // Lower 4 bits
  }

  /**
   * Decodes the Tier from the given encoded int.
   */
  public static Tier decodeTier(int customModelData) {
    return Tier.values()[(customModelData >> 4) & 0xF];  // Next 4 bits
  }

  /**
   * Creates an ItemStack from the given Material and Tool.
   */
  public static ItemStack getItemStack(Material material, Tool tool) {
    ItemStack result = new ItemStack(material, 1);

    // Set ?eta
    ItemMeta meta = result.getItemMeta();
    meta.setCustomModelData(tool.getCustomModelData());
    meta.setDisplayName(ChatColor.RESET + tool.getDisplayName(material));
    meta.setLore(tool.getLore());
    result.setItemMeta(meta);

    return result;

  }

  public static ItemStack getItemStack(Material material, Type type, Tier tier) {
    return getItemStack(material, Tool.getTool(type, tier));
  }
}
