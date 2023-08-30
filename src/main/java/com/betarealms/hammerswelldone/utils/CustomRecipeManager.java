package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.definitions.CustomRecipeDefinitions;
import com.betarealms.hammerswelldone.objects.CustomRecipe;
import com.betarealms.hammerswelldone.types.Type;
import java.util.List;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

/**
 * This class handles all custom recipe management logic.
 */
public class CustomRecipeManager implements Listener {
  private final List<CustomRecipe> customRecipes;

  public CustomRecipeManager() {
    this.customRecipes = CustomRecipeDefinitions.getRecipes();
  }

  /**
   * This method is called when a player changes items in workbench.
   *
   * @param event prepare item
   */
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
    // If both items are null, they are the same
    if (layoutItem == null && invItem == null) {
      return true;
    }

    // If only one of them is null, they are not
    if (layoutItem == null || invItem == null) {
      return false;
    }

    // Prevent NullPointerException
    if (layoutItem.getItemMeta() == null
        || invItem.getItemMeta() == null) {
      return false;
    }

    // Check if custom model data of layoutItem and invItem are different
    if (layoutItem.getItemMeta().hasCustomModelData()
          && (!invItem.getItemMeta().hasCustomModelData()
          || invItem.getItemMeta().getCustomModelData()
              != layoutItem.getItemMeta().getCustomModelData())) {
      return false;
    }

    // Check if custom model data of layoutItem and invItem are the same
    if (invItem.getItemMeta().hasCustomModelData()
          && !layoutItem.getItemMeta().hasCustomModelData()) {
      return false;
    }

    return layoutItem.getType().equals(invItem.getType());
  }

  /**
   * This is used to block upgrading SUPER tools to netherite.
   *
   * @param event PrepareItemCraftEvent
   */
  @EventHandler
  public void onPrepareSmithing(PrepareSmithingEvent event) {
    // Get the items in the input slots
    ItemStack[] inputs = event.getInventory().getContents();

    // Check if item in slot 1 is a SUPER tool
    if (inputs[1] != null
        && ToolManager.isCustomTool(Objects.requireNonNull(inputs[1].getItemMeta()))
        && ToolManager.decodeType(inputs[1].getItemMeta().getCustomModelData()) == Type.SUPER) {
      event.setResult(null);
    }
  }
}
