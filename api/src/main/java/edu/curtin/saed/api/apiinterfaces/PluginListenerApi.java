package edu.curtin.saed.api.apiinterfaces;

import edu.curtin.saed.api.model.GameData;

/*
 * Plug in listener that is implemented by all plugin classes
 */

public interface PluginListenerApi {

    void onPlayerMove(GameData data, double newX, double newY);
    void onItemAcquired(GameData data, String itemName);
    void onPluginActionSelected(GameData data);
    String getPluginName();
}
