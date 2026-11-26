package edu.curtin.saed.api.apiinterfaces;

import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.Item;

/*
 * Api for all game logic functions
 */

public interface GameLogicApi {
  
  void movePlayer(char direction, GameData data);
  void itemPickup(GameData data, Item item);
  void exitGame(GameData data);
  void firePluginActionSelected(GameData data, String pluginName);
}
