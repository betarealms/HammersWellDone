package com.betarealms.hammerswelldone.definitions;

import static java.lang.Character.forDigit;

import com.betarealms.hammerswelldone.objects.CustomRecipe;
import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Type;
import com.betarealms.hammerswelldone.utils.ToolManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This class contains all custom recipes.
 */
public class CustomRecipeDefinitions {
  private CustomRecipeDefinitions() {
    // This constructor is intentionally empty. Nothing to initialize.
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  // Initialize Tier ADVANCED and GOD recipes except for SUPER
  private static void initializeTools(
      List<CustomRecipe> recipes, HashMap<String, Material[]> materials) {
    // Iterate through all tools except for SUPER (vanilla tools)
    for (Type type : new Type[] {Type.PICKAXE, Type.SHOVEL, Type.AXE, Type.HOE}) {
      // Iterate through all tiers except for VANILLA
      for (Tier tier : new Tier[] {Tier.ADVANCED, Tier.GOD}) {
        // Iterate through all materials
        for (Map.Entry<String, Material[]> entry : materials.entrySet()) {
          // Create a new custom recipe
          CustomRecipe customRecipe = new CustomRecipe(
              ToolManager.getItemStack(Material.getMaterial(
                  entry.getKey() + "_" + type.name()), type, tier));

          // Add an ingot or a block depending on the tier
          customRecipe.setIngredient('V', new ItemStack(entry.getValue()[tier.getBit() - 1]));
          // Add an item
          customRecipe.setIngredient('I', new ItemStack(entry.getValue()[0]));
          // Add the center tool
          customRecipe.setIngredient('T', tier == Tier.ADVANCED
              ? new ItemStack(
                  getMaterialFromType(entry.getKey(), type))
              : ToolManager.getItemStack(
                  getMaterialFromType(entry.getKey(), type), type, Tier.ADVANCED));
          // Add the stick
          customRecipe.setIngredient('S', new ItemStack(Material.STICK));

          // Add the shape
          customRecipe.shape(
              "VI ",
              "ITI",
              "SIV"
          );

          // Save the recipe
          recipes.add(customRecipe);
        }
      }
    }
  }

  private static void initializeSupers(
      List<CustomRecipe> recipes, HashMap<String, Material[]> materials) {
    // Iterate through all tiers
    for (Tier tier : Tier.values()) {
      // Iterate through all materials
      for (Map.Entry<String, Material[]> entry : materials.entrySet()) {
        // Create a new custom recipe (use SWORD for the SUPER)
        CustomRecipe customRecipe = new CustomRecipe(
            ToolManager.getItemStack(
                Material.getMaterial(entry.getKey() + "_SWORD"), Type.SUPER, tier));

        // Add an ingot
        customRecipe.setIngredient('I', new ItemStack(entry.getValue()[0]));
        // Add a block
        customRecipe.setIngredient('B', new ItemStack(entry.getValue()[1]));

        // Define vanilla types
        final Type[] vanillaTypes
            = new Type[] {Type.PICKAXE, Type.SHOVEL, Type.AXE, Type.HOE};

        // Add the four tools
        for (int i = 1; i < 5; i++) {
          customRecipe.setIngredient(forDigit(i, 10), tier == Tier.VANILLA
              ? new ItemStack(
                getMaterialFromType(entry.getKey(), vanillaTypes[i - 1]))
              : ToolManager.getItemStack(
                getMaterialFromType(entry.getKey(), vanillaTypes[i - 1]),
                vanillaTypes[i - 1], tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD)
          );
        }

        // Add the shape
        customRecipe.shape(
            "I1I",
            "2B3",
            "I4I"
        );

        // Save the recipe
        recipes.add(customRecipe);
      }
    }
  }

  /**
   * Get List of all custom recipes.
   *
   * @return List of custom recipes
   */
  public static List<CustomRecipe> getRecipes() {
    // Define materials
    final HashMap<String, Material[]> materials = new HashMap<>(5);
    materials.put("WOODEN", new Material[] {Material.OAK_PLANKS, Material.OAK_LOG});
    materials.put("STONE", new Material[] {Material.COBBLESTONE, Material.STONE});
    materials.put("IRON", new Material[] {Material.IRON_INGOT, Material.IRON_BLOCK});
    materials.put("GOLDEN", new Material[] {Material.GOLD_INGOT, Material.GOLD_BLOCK});
    materials.put("DIAMOND", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});
    materials.put("NETHERITE", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});

    // Initialize recipes
    List<CustomRecipe> recipes = new ArrayList<>();

    // Initialize Tier ADVANCED and GOD recipes except for SUPER
    initializeTools(recipes, materials);

    // Initialize SUPER recipes
    initializeSupers(recipes, materials);

    return recipes;
  }

  private static Material getMaterialFromType(String material, Type type) {
    return Material.getMaterial(material + "_" + type.name());
  }
}
