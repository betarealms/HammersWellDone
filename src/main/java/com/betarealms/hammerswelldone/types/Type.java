package com.betarealms.hammerswelldone.types;

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