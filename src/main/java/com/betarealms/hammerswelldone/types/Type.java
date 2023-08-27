package com.betarealms.hammerswelldone.types;

/**
 * This enum contains all the Types available, table below.
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
public enum Type {
  PICKAXE(0), // Bit: 0000
  SHOVEL(1),  // Bit: 0001
  AXE(2),     // Bit: 0010
  HOE(3),     // Bit: 0011
  SUPER(4),   // Bit: 0100
  ;

  private final int bit;

  Type(int bit) {
    this.bit = bit;
  }

  public int getBit() {
    return bit;
  }
}