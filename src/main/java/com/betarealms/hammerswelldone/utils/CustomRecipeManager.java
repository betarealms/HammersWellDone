package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.definitions.CustomRecipeDefinitions;
import com.betarealms.hammerswelldone.objects.CustomRecipe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class CustomRecipeManager implements Listener {
  private final List<CustomRecipe> customRecipes;

  public CustomRecipeManager() {
    this.customRecipes = CustomRecipeDefinitions.getRecipes();
  }

  @EventHandler
  public void onCraftItem(PrepareItemCraftEvent event) {
    CraftingInventory inv = event.getInventory();
    for (CustomRecipe recipe : customRecipes) {
      if (isMatchingRecipe(inv, recipe)) {
        inv.setResult(recipe.getOutput());
        return;
      }
    }
  }

  private boolean isMatchingRecipe(CraftingInventory inv, CustomRecipe recipe) {
    ItemStack[][] layout = recipe.getLayout();
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        ItemStack invItem = inv.getItem(row * 3 + col + 1);
        ItemStack layoutItem = layout[row][col];
        if (!isMatchingItem(invItem, layoutItem)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isMatchingItem(ItemStack invItem, ItemStack layoutItem) {
    if (layoutItem == null && invItem == null) {
      return true;
    }
    if (layoutItem == null || invItem == null) {
      return false;
    }
    if (layoutItem.getItemMeta().hasCustomModelData()
          && (!invItem.getItemMeta().hasCustomModelData()
          || invItem.getItemMeta().getCustomModelData() != layoutItem.getItemMeta().getCustomModelData())) {
      return false;
    }
    if (invItem.getItemMeta().hasCustomModelData()
          && !layoutItem.getItemMeta().hasCustomModelData()) {
      return false;
    }
    if (layoutItem.getType().equals(invItem.getType())) {
      return true;
    }

    return false;
  }
}
