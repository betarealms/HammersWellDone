package com.betarealms.hammerswelldone.events;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;

/**
 * Triggers when a player joins.
 */
public class OnPlayerJoin implements Listener {
  /**
   * Handles adding recipes to all players.
   *
   * @param event PlayerJoinEvent
   */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    Iterator<Recipe> recipes = Bukkit.recipeIterator();
    while (recipes.hasNext()) {
      Recipe recipe = recipes.next();
      if (!(recipe instanceof Keyed keyedRecipe)) {
        return;
      }
      if (!keyedRecipe.getKey().toString().contains("HWD_")) {
        return;
      }
      player.discoverRecipe(keyedRecipe.getKey());
    }
  }
}
