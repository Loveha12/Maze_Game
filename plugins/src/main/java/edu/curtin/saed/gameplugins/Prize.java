package edu.curtin.saed.gameplugins;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.Item;

/*
 * Prize plugin, specified in the assignment requirements
 */

public class Prize implements PluginListenerApi {

  String pluginName = "Prize";
  int accumulatedItems = 0;
  boolean running = false;

  @Override
  public void onPlayerMove(GameData data, double newX, double newY) {/* not used in this plugin */}

  @Override
  public void onItemAcquired(GameData data, String itemName) {
    if (accumulatedItems >= 5 && running) {
      System.out.println("You earned a prize!");
      data.getPlayer().addItemToInventory(new Item("Special Vial", null, "Contains powerful strength boosting ingredients."));
      running = false;
      accumulatedItems = 0;
    }
    else if (running) {
      accumulatedItems++;
    }
    else {
      accumulatedItems = 0;
    }
  }

  @Override
  public void onPluginActionSelected(GameData data) {
    if (!this.running) {
      this.running = true;
      accumulatedItems = 0;
    }
    else {
      this.running = false;
      System.out.println(pluginName + " shutting down...");
    }
  }

  @Override
  public String getPluginName() {
    return pluginName;
  }
  
}
