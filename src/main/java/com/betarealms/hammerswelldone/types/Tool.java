package com.betarealms.hammerswelldone.types;

import com.betarealms.hammerswelldone.utils.UtilTool;

public enum Tool {

  // Pickaxe family
  PICKAXE(Type.PICKAXE, Tier.VANILLA),
  HAMMER(Type.PICKAXE, Tier.ADVANCED),
  MJOLNIR(Type.PICKAXE, Tier.GOD),

  // Shovel family
  SHOVEL(Type.SHOVEL, Tier.VANILLA),
  EXCAVATOR(Type.SHOVEL, Tier.ADVANCED),
  TERRAFORMER(Type.SHOVEL, Tier.GOD),

  // Axe family
  AXE(Type.AXE, Tier.VANILLA),
  LUMBER_AXE(Type.AXE, Tier.ADVANCED),
  LUMBERON(Type.AXE, Tier.GOD),

  // Hoe family
  HOE(Type.HOE, Tier.VANILLA),
  MATTOCK(Type.HOE, Tier.ADVANCED),
  CROPMASTER(Type.HOE, Tier.GOD),

  // Super family
  HANDYTOOL(Type.SUPER, Tier.VANILLA),
  MULTITOOL(Type.SUPER, Tier.ADVANCED),
  DEITYTOOL(Type.SUPER, Tier.GOD),
  ;

  private final Type type;
  private final Tier tier;

  Tool(Type type, Tier tier) {
    this.type = type;
    this.tier = tier;
  }

  public int getCustomModelData() {
    return UtilTool.encodeCustomModelData(type, tier);
  }

  public Type getType() {
    return type;
  }

  public Tier getTier() {
    return tier;
  }
}

