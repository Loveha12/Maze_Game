package edu.curtin.saed.api.apiinterfaces;

import java.util.List;
import edu.curtin.saed.api.model.GameData;

/* 
 * Un-used api for the Game configuration
 */
public interface GameConfigApi {

  void setGridSize(GameData data, double x, double y);
  void setPlayerStart(GameData data, double x, double y);
  void setGoalLocation(GameData data, double x, double y);
  void addItemToGame(GameData data, double x, double y, String itemName, String message);
  void addObstacleToGame(GameData data, double x, double y, List<String> requiredItemNames);
}
