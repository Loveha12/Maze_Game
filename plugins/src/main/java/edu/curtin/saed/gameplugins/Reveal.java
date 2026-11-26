package edu.curtin.saed.gameplugins;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridAreaIcon;

/*
 * Reveal plugin, specified in the assignment requirements
 */

public class Reveal implements PluginListenerApi {

  String pluginName = "Reveal";
  boolean running = false;

  @Override
  public void onPlayerMove(GameData data, double newX, double newY) {
    /* not used by this plugin */}

  @Override
  public void onItemAcquired(GameData data, String itemName) {
    if (itemName.equals("Map") && running) {
      for (GridAreaIcon icon : data.getArea().getIcons()) {
        if (!icon.getCaption().equals("Obstacle")) {
          icon.setShown(true);
        }
      }
    }
  }

  @Override
  public void onPluginActionSelected(GameData data) {
    if (!this.running) {
      this.running = true;
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
