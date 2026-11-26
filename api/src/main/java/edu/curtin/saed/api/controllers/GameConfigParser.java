package edu.curtin.saed.api.controllers;

import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridAreaIcon;
import edu.curtin.saed.api.model.Item;
import edu.curtin.saed.api.model.Obstacle;

import java.io.InputStream;
import java.util.List;

import edu.curtin.saed.api.apiinterfaces.GameConfigApi;

/*
 * Not used unfortunately, I never bothered changing over to this implementation for 
 * the initialisation of the game data since it already works crudely with the ConfigReader.java
 */
public class GameConfigParser implements GameConfigApi {

  @Override
  public void setGridSize(GameData data, double x, double y) {
    data.getArea().setGridSize(x, y);
  }

  @Override
  public void setPlayerStart(GameData data, double x, double y) {
    data.getPlayer().getIcon().setPosition(x, y);
  }

  @Override
  public void setGoalLocation(GameData data, double x, double y) {
    data.getArea().getIcons().get(0).setPosition(x, y);
  }

  @Override
  public void addItemToGame(GameData data, double x, double y, String itemName, String message) {
    InputStream imageStream = GameData.class.getClassLoader().getResourceAsStream("item.png");
    Item newItem = new Item(itemName, new GridAreaIcon( x, y, 0.0, 1.0, imageStream , itemName), message);
    data.getItems().add(newItem);
    data.getArea().getIcons().add(newItem.getIcon());
  }

  @Override
  public void addObstacleToGame(GameData data, double x, double y, List<String> requiredItemNames) {
    InputStream imageStream = GameData.class.getClassLoader().getResourceAsStream("obstacle.png");
    Obstacle newObstacle = new Obstacle(new GridAreaIcon(x, y, 0.0, 1.0, imageStream, "Obstacle"), requiredItemNames);
    data.getObstacles().add(newObstacle);
    data.getArea().getIcons().add(newObstacle.getIcon());
  }
}
