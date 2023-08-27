package com.betarealms.hammerswelldone;

import com.betarealms.hammerswelldone.events.OnCommand;
import com.betarealms.hammerswelldone.events.OnTabComplete;
import com.betarealms.hammerswelldone.utils.CustomRecipeManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class HammersWellDone extends JavaPlugin {

  // On plugin enable
  @Override
  public void onEnable() {

    // Register commands
    getServer().getPluginCommand("hwd").setExecutor(new OnCommand());
    getServer().getPluginCommand("hwd").setTabCompleter(new OnTabComplete());

    // Register recipe manager
    getServer().getPluginManager().registerEvents(new CustomRecipeManager(), this);

    getLogger().info("Enabled");
  }

  // On plugin disable
  @Override
  public void onDisable() {

    getLogger().info("Disabled");
  }
  public static ItemStack createHammer() {


    return null;
  }
}
