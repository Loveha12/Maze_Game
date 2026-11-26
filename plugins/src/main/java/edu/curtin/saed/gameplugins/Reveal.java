package edu.curtin.saed.gameplugins;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridArea;
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
    /* not used by this plugin */
  }

  @Override
  public void onPluginActionSelected(GameData data) {
    for (GridAreaIcon icon : data.getArea().getIcons()) {
      data.getArea().setSquareColor(icon.getX(), icon.getY(), true);
    }
    data.getArea().setVisible(true);
  }

  @Override
  public String getPluginName() {
    return pluginName;
  }
}
