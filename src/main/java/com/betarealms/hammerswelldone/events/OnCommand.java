package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.HammersWellDone;
import com.betarealms.hammerswelldone.types.Tool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OnCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    // Check if the command is executed by a player
    /*if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
      return true;
    }*/

    // Debug
    for (Tool tool : Tool.values()) {
      org.bukkit.plugin.java.JavaPlugin.getPlugin(HammersWellDone.class).getLogger().info(tool.toString());
      org.bukkit.plugin.java.JavaPlugin.getPlugin(HammersWellDone.class).getLogger().info("- ID: " + tool.getCustomModelData());
      org.bukkit.plugin.java.JavaPlugin.getPlugin(HammersWellDone.class).getLogger().info("- Type: " + tool.getType());
      org.bukkit.plugin.java.JavaPlugin.getPlugin(HammersWellDone.class).getLogger().info("- Tier: " + tool.getTier());
    }

    return true;
  }
}
