package com.betarealms.hammerswelldone.definitions;

import com.betarealms.hammerswelldone.objects.CustomRecipe;
import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Type;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CustomRecipeDefinitions {
  public static List<CustomRecipe> getRecipes() {
    List<CustomRecipe> recipes = new ArrayList<>();

    // Initialize Tier ADVANCED and GOD recipes except for SUPER
    for (Type type : new Type[] {Type.PICKAXE, Type.SHOVEL, Type.AXE, Type.HOE}) {
      for (Tier tier : new Tier[] {Tier.ADVANCED, Tier.GOD}) {
        for (Map.Entry<String, Material[]> entry : new java.util.HashMap<String, Material[]>() {{
          put("WOODEN", new Material[] {Material.OAK_WOOD, Material.OAK_LOG});
          put("STONE", new Material[] {Material.COBBLESTONE, Material.STONE});
          put("IRON", new Material[] {Material.IRON_INGOT, Material.IRON_BLOCK});
          put("GOLDEN", new Material[] {Material.GOLD_INGOT, Material.GOLD_BLOCK});
          put("DIAMOND", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});
        }}.entrySet()) {
          CustomRecipe customRecipe = new CustomRecipe(ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_" + type.name()), type, tier));

          customRecipe.setIngredient('V', new ItemStack(entry.getValue()[tier.getBit() - 1]));
          customRecipe.setIngredient('I', new ItemStack(entry.getValue()[0]));
          customRecipe.setIngredient('T', tier == Tier.ADVANCED ? new ItemStack(Material.getMaterial(entry.getKey() + "_" + type.name())) : ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_" + type.name()), type, Tier.ADVANCED));
          customRecipe.setIngredient('S', new ItemStack(Material.STICK));

          customRecipe.shape(
              "VI ",
              "ITI",
              "SIV"
          );

          recipes.add(customRecipe);
        }
      }
    }

    // Initialize SUPER recipes
    for (Tier tier : Tier.values()) {
      for (Map.Entry<String, Material[]> entry : new java.util.HashMap<String, Material[]>() {{
        put("WOODEN", new Material[] {Material.OAK_WOOD, Material.OAK_LOG});
        put("STONE", new Material[] {Material.COBBLESTONE, Material.STONE});
        put("IRON", new Material[] {Material.IRON_INGOT, Material.IRON_BLOCK});
        put("GOLDEN", new Material[] {Material.GOLD_INGOT, Material.GOLD_BLOCK});
        put("DIAMOND", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});
        put("NETHERITE", new Material[] {Material.NETHER_BRICK, Material.NETHERITE_BLOCK});
      }}.entrySet()) {
        CustomRecipe customRecipe = new CustomRecipe(ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_SWORD"), Type.SUPER, tier));

        customRecipe.setIngredient('I', new ItemStack(entry.getValue()[0]));
        customRecipe.setIngredient('B', new ItemStack(entry.getValue()[1]));
        customRecipe.setIngredient('1', tier == Tier.VANILLA ? new ItemStack(Material.getMaterial(entry.getKey() + "_PICKAXE")) : ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_PICKAXE"), Type.PICKAXE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD));
        customRecipe.setIngredient('2', tier == Tier.VANILLA ? new ItemStack(Material.getMaterial(entry.getKey() + "_SHOVEL")) : ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_SHOVEL"), Type.SHOVEL, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD));
        customRecipe.setIngredient('3', tier == Tier.VANILLA ? new ItemStack(Material.getMaterial(entry.getKey() + "_AXE")) : ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_AXE"), Type.AXE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD));
        customRecipe.setIngredient('4', tier == Tier.VANILLA ? new ItemStack(Material.getMaterial(entry.getKey() + "_HOE")) : ToolManager.getItemStack(Material.getMaterial(entry.getKey() + "_HOE"), Type.HOE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD));

        customRecipe.shape(
            "I1I",
            "2B3",
            "I4I"
        );

        recipes.add(customRecipe);
      }
    }

    return recipes;
  }
}
