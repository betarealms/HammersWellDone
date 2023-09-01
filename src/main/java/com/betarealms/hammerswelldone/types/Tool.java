package com.betarealms.hammerswelldone.types;

import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * This enum contains all the Tools available, table below.
 * +---------+--------------+---------------------+----------------+
 * |  Type   | Vanilla Tier | Advanced Tier (3x3) | God Tier (5x5) |
 * +---------+--------------+---------------------+----------------+
 * | Pickaxe | Pickaxe      | Hammer              | Mjolnir        |
 * | Shovel  | Shovel       | Excavator           | Terraformer    |
 * | Axe     | Axe          | Lumber Axe          | Lumberon       |
 * | Hoe     | Hoe          | Mattock             | Cropmaster     |
 * | Super   | HandyTool    | MultiTool           | DeityTool      |
 * +---------+--------------+---------------------+----------------+
 */
public enum Tool {

  // Pickaxe family
  PICKAXE(Type.PICKAXE, Tier.VANILLA, "Pickaxe"),
  HAMMER(Type.PICKAXE, Tier.ADVANCED, "Hammer"),
  MJOLNIR(Type.PICKAXE, Tier.GOD, "Mjolnir"),

  // Shovel family
  SHOVEL(Type.SHOVEL, Tier.VANILLA, "Shovel"),
  EXCAVATOR(Type.SHOVEL, Tier.ADVANCED, "Excavator"),
  TERRAFORMER(Type.SHOVEL, Tier.GOD, "Terraformer"),

  // Axe family
  AXE(Type.AXE, Tier.VANILLA, "Axe"),
  LUMBER_AXE(Type.AXE, Tier.ADVANCED, "Lumber Axe"),
  LUMBERON(Type.AXE, Tier.GOD, "Lumberon"),

  // Hoe family
  HOE(Type.HOE, Tier.VANILLA, "Hoe"),
  MATTOCK(Type.HOE, Tier.ADVANCED, "Mattock"),
  CROPMASTER(Type.HOE, Tier.GOD, "Cropmaster"),

  // Super family
  HANDYTOOL(Type.SUPER, Tier.VANILLA, "HandyTool"),
  MULTITOOL(Type.SUPER, Tier.ADVANCED, "MultiTool"),
  DEITYTOOL(Type.SUPER, Tier.GOD, "DeityTool"),
  ;

  // Init variables
  private final Type type;
  private final Tier tier;
  private final String name;
  private static final Map<String, String> materialNames;
  private static final Map<Tier, ChatColor> color;
  private static final Map<Type, String> loreType;
  private static final Map<Tier, String> loreTier;

  // Get strings for naming
  static {
    materialNames = new HashMap<>();
    materialNames.put("WOODEN", "Wooden");
    materialNames.put("STONE", "Stone");
    materialNames.put("IRON", "Iron");
    materialNames.put("GOLDEN", "Golden");
    materialNames.put("DIAMOND", "Diamond");
    materialNames.put("NETHERITE", "Netherite");
  }

  // Get strings for Lore based on Tier
  static {
    color = new EnumMap<>(Tier.class);
    color.put(Tier.VANILLA, ChatColor.YELLOW);
    color.put(Tier.ADVANCED, ChatColor.AQUA);
    color.put(Tier.GOD, ChatColor.LIGHT_PURPLE);
  }

  // Get strings for Lore based on Type
  static {
    loreType = new EnumMap<>(Type.class);
    loreType.put(Type.PICKAXE, "This is a Pickaxe substitute.");
    loreType.put(Type.SHOVEL, "This is a Shovel substitute.");
    loreType.put(Type.AXE, "This is an Axe substitute.");
    loreType.put(Type.HOE, "This is a Hoe substitute.");
    loreType.put(Type.SUPER, "This can mine as all tools in one.");
  }

  // Get strings for Lore based on Tier
  static {
    loreTier = new EnumMap<>(Tier.class);
    loreTier.put(Tier.VANILLA, null);
    loreTier.put(Tier.ADVANCED, "It covers area of 3x3 blocks.");
    loreTier.put(Tier.GOD, "It covers area of 5x5 blocks.");
  }

  Tool(Type type, Tier tier, String name) {
    this.type = type;
    this.tier = tier;
    this.name = name;
  }

  public int getCustomModelData() {
    return ToolManager.encodeCustomModelData(type, tier);
  }

  public Type getType() {
    return type;
  }

  public Tier getTier() {
    return tier;
  }

  /**
   * Generates and returns the display name of the tool based on the provided Material and Tier.
   *
   * @param material The Material of the base vanilla tool
   * @return A String representing the formatted display name
   */
  public String getDisplayName(Material material) {
    // Split the string into material and tool parts
    String[] parts = material.name().split("_", 2);
    if (parts.length < 2) {
      return "Unknown " + this.name();
    }
    return color.get(this.tier) + materialNames.get(parts[0]) + " " + this.name;
  }

  /**
   * Returns the lore of the tool based on the provided Material and Tier.
   *
   * @return List of Strings representing the lore
   */
  public List<String> getLore() {
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.GRAY + loreType.get(this.type));
    if (loreTier.get(this.tier) != null) {
      lore.add(ChatColor.GRAY + loreTier.get(this.tier));
    }
    return lore;
  }

  /**
   * Returns the Tool based on the provided Material and Tier.
   *
   * @param type Tool type
   * @param tier Tool tier
   * @return a Tool
   */
  public static Tool getTool(Type type, Tier tier) {
    for (Tool tool : Tool.values()) {
      if (tool.getType() == type && tool.getTier() == tier) {
        return tool;
      }
    }
    return null;
  }
}

