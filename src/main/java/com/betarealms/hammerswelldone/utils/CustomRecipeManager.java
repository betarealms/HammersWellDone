package com.betarealms.hammerswelldone.utils;

import com.betarealms.hammerswelldone.HammersWellDone;
import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Type;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class handles all custom recipe management logic.
 */
public class CustomRecipeManager {

  /**
   * This class is used to initialize all the custom recipes.
   */
  public static void initializeRecipes() {
    // Initialize materials
    final HashMap<String, Material[]> materials = initializeMaterials();

    // Initialize all tools except SUPER
    initializeTools(materials);

    // Initialize SUPERs separately, since they have a different recipe
    initializeSupers(materials);
  }

  // Initialize SUPERs
  private static void initializeSupers(HashMap<String, Material[]> materials) {
    // Get plugin instance
    Plugin plugin = JavaPlugin.getPlugin(HammersWellDone.class);
    // Iterate through all tiers
    for (Tier tier : Tier.values()) {
      // Iterate through all materials
      for (Map.Entry<String, Material[]> entry : materials.entrySet()) {
        // Get output item (use SWORD for the SUPER)
        ItemStack outputItem = ToolManager.getItemStack(
            Material.getMaterial(entry.getKey() + "_SWORD"), Type.SUPER, tier);

        // Get input pickaxe
        ItemStack inputPickaxe = tier == Tier.VANILLA
            ? new ItemStack(
            getMaterialFromType(entry.getKey(), Type.PICKAXE))
            : ToolManager.getItemStack(
            getMaterialFromType(entry.getKey(), Type.PICKAXE),
            Type.PICKAXE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD);

        // Get input shovel
        ItemStack inputShovel = tier == Tier.VANILLA
            ? new ItemStack(
            getMaterialFromType(entry.getKey(), Type.SHOVEL))
            : ToolManager.getItemStack(
            getMaterialFromType(entry.getKey(), Type.SHOVEL),
            Type.SHOVEL, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD);

        // Get input axe
        ItemStack inputAxe = tier == Tier.VANILLA
            ? new ItemStack(
            getMaterialFromType(entry.getKey(), Type.AXE))
            : ToolManager.getItemStack(
            getMaterialFromType(entry.getKey(), Type.AXE),
            Type.AXE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD);

        // Get input hoe
        ItemStack inputHoe = tier == Tier.VANILLA
            ? new ItemStack(
            getMaterialFromType(entry.getKey(), Type.HOE))
            : ToolManager.getItemStack(
            getMaterialFromType(entry.getKey(), Type.HOE),
            Type.HOE, tier == Tier.ADVANCED ? Tier.ADVANCED : Tier.GOD);

        // Create a new key
        NamespacedKey key = new NamespacedKey(plugin,
            entry.getKey() + "_SUPER_" + tier.name());

        // Create a custom recipe
        ShapedRecipe recipe = new ShapedRecipe(key, outputItem);

        // Add recipe shape
        recipe.shape("MPM", "SBA", "MHM");

        // Add recipe ingredients
        // TO DO: Wood in M and B
        // Add a material
        recipe.setIngredient('M', entry.getValue()[0]);

        // Add a block
        recipe.setIngredient('B', entry.getValue()[1]);

        // Add a pickaxe
        recipe.setIngredient('P', new RecipeChoice.ExactChoice(inputPickaxe));

        // Add a shovel
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(inputShovel));

        // Add an axe
        recipe.setIngredient('A', new RecipeChoice.ExactChoice(inputAxe));

        // Add a hoe
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(inputHoe));

        // Add the recipe
        Bukkit.addRecipe(recipe);
      }
    }
  }

  // Initialize ADVANCED and GOD tools except for SUPERs
  private static void initializeTools(HashMap<String, Material[]> materials) {
    // Get plugin instance
    Plugin plugin = JavaPlugin.getPlugin(HammersWellDone.class);
    // Iterate through all tools except for SUPER (vanilla tools)
    for (Type type : new Type[] {Type.PICKAXE, Type.SHOVEL, Type.AXE, Type.HOE}) {
      // Iterate through all tiers except for VANILLA
      for (Tier tier : new Tier[] {Tier.ADVANCED, Tier.GOD}) {
        // Iterate through all materials
        for (Map.Entry<String, Material[]> entry : materials.entrySet()) {
          // Get output item
          ItemStack outputItem = ToolManager.getItemStack(Material.getMaterial(
              entry.getKey() + "_" + type.name()), type, tier);

          // Get input item
          ItemStack inputItem = tier == Tier.ADVANCED
              ? new ItemStack(
              getMaterialFromType(entry.getKey(), type))
              : ToolManager.getItemStack(
              getMaterialFromType(entry.getKey(), type), type, Tier.ADVANCED);

          // Create a new key
          NamespacedKey key = new NamespacedKey(plugin,
              entry.getKey() + "_" + type.name() + "_" + tier.name());

          // Create a custom recipe
          ShapedRecipe recipe = new ShapedRecipe(key, outputItem);

          // Add recipe shape
          recipe.shape("VM ", "MTM", "SMV");

          // Add recipe ingredients
          // Add a material or a block depending on the tier - TO DO: WOOD
          recipe.setIngredient('V', entry.getValue()[tier.getBit() - 1]);

          // Add a material
          recipe.setIngredient('M', entry.getValue()[0]);

          // Add a center tool
          recipe.setIngredient('T', new RecipeChoice.ExactChoice(inputItem));

          // Add a stick
          recipe.setIngredient('S', Material.STICK);

          // Add the recipe
          Bukkit.addRecipe(recipe);
        }
      }
    }
  }

  // Initialize materials used to craft the tools
  private static HashMap<String, Material[]> initializeMaterials() {
    final HashMap<String, Material[]> materials = new HashMap<>(5);
    materials.put("WOODEN", new Material[] {Material.OAK_PLANKS, Material.OAK_LOG});
    materials.put("STONE", new Material[] {Material.COBBLESTONE, Material.STONE});
    materials.put("IRON", new Material[] {Material.IRON_INGOT, Material.IRON_BLOCK});
    materials.put("GOLDEN", new Material[] {Material.GOLD_INGOT, Material.GOLD_BLOCK});
    materials.put("DIAMOND", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});
    materials.put("NETHERITE", new Material[] {Material.DIAMOND, Material.DIAMOND_BLOCK});
    return materials;
  }

  // Gets material from string and type
  private static Material getMaterialFromType(String material, Type type) {
    return Material.getMaterial(material + "_" + type.name());
  }
}
