package com.betarealms.hammerswelldone;

import com.betarealms.hammerswelldone.events.OnBlockBreak;
import com.betarealms.hammerswelldone.events.OnPlayerJoin;
import com.betarealms.hammerswelldone.events.OnPrepareSmithing;
import com.betarealms.hammerswelldone.utils.CustomRecipeManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
@SuppressWarnings("unused") // This class is actually used by the Spigot API
public final class HammersWellDone extends JavaPlugin {

  // On plugin enable
  @Override
  public void onEnable() {
    // Register events
    getServer().getPluginManager().registerEvents(new OnPrepareSmithing(), this);
    getServer().getPluginManager().registerEvents(new OnBlockBreak(), this);
    getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);

    // Initialize recipes
    CustomRecipeManager.initializeRecipes();

    getLogger().info("Enabled");
  }

  // On plugin disable
  @Override
  public void onDisable() {
    getLogger().info("Disabled");
  }
}
