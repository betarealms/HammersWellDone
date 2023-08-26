package com.betarealms.hammerswelldone.types;

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