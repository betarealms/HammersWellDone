package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Type;

public class UtilTool {

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
}
