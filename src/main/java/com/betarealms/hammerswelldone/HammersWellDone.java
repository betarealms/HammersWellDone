package com.betarealms.hammerswelldone;

import com.betarealms.hammerswelldone.events.OnBlockBreak;
import com.betarealms.hammerswelldone.events.OnPrepareSmithing;
import com.betarealms.hammerswelldone.utils.CustomRecipeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

    // Initialize recipes
    CustomRecipeManager.initializeRecipes();

    getLogger().info("Enabled");

    // Todo: DEBUG
    getServer().getPluginCommand("hwd").setExecutor(this);
    getServer().getPluginCommand("hwd").setTabCompleter(this);
    // Todo: END DEBUG
  }

  // On plugin disable
  @Override
  public void onDisable() {
    getLogger().info("Disabled");
  }

  // Todo: DEBUG
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // Check if the command is executed by a player
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
      return true;
    }

    Player player = (Player) sender;
    player.sendMessage("Executed /hwd");

    return true;
  }
  // Todo: END DEBUG
}
