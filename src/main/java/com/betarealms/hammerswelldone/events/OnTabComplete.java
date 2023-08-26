package com.betarealms.hammerswelldone.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class OnTabComplete implements TabCompleter {

  // Tab completer
  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                    String[] args) {

    List<String> options = new ArrayList<>();

    return options;
  }
}
