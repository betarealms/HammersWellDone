package com.betarealms.hammerswelldone.types;

/**
 * This enum contains all the Tiers available, table below.
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
public enum Tier {
  VANILLA(0),     // Bit: 0000
  ADVANCED(1),    // Bit: 0001
  GOD(2),         // Bit: 0010
  ;

  private final int bit;

  Tier(int bit) {
    this.bit = bit;
  }

  public int getBit() {
    return bit;
  }
}