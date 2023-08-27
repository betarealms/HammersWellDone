package com.betarealms.hammerswelldone.objects;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

/**
 * This is an object for Custom Recipes.
 */
public class CustomRecipe {
  private final ItemStack output;
  private final ItemStack[][] layout = new ItemStack[3][3];
  private final Map<Character, ItemStack> charMap = new HashMap<>();

  public CustomRecipe(ItemStack output) {
    this.output = output;
  }

  /**
   * This defines the shape of the custom recipe.
   *
   * @param rows Three rows
   */
  public void shape(String... rows) {
    for (int i = 0; i < rows.length; i++) {
      for (int j = 0; j < rows[i].length(); j++) {
        char c = rows[i].charAt(j);
        layout[i][j] = charMap.get(c);
      }
    }
  }

  public void setIngredient(char c, ItemStack item) {
    charMap.put(c, item);
  }

  public ItemStack getOutput() {
    return output;
  }

  public ItemStack[][] getLayout() {
    return layout;
  }
}
