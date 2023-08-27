package com.betarealms.hammerswelldone.events;

import com.betarealms.hammerswelldone.types.Tier;
import com.betarealms.hammerswelldone.types.Tool;
import com.betarealms.hammerswelldone.utils.ToolManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    // Check if the command is executed by a player
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
      return true;
    }

    // Debug
    Player player = (Player) sender;
    player.getInventory().addItem(ToolManager.getItemStack(Material.WOODEN_HOE, Tool.HANDYTOOL));

    player.sendMessage(Tier.GOD.getBit() + "");

    return true;
  }
}
