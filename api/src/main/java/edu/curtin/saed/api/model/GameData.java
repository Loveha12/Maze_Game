package edu.curtin.saed.api.model;

import java.util.ArrayList;
import java.util.List;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;

/* 
 * Main housing for all of the games data 
 */
public class GameData {

  private Player player;
  private GridAreaIcon goal;
  private GridArea area; // Actual game area
  private List<Obstacle> obstacles; // List of all obstacles in the game
  private List<Item> items; // List of al items on the ground
  private List<PluginListenerApi> listeners = new ArrayList<>(); // All loaded plugins
  private boolean running = true;
  private int daysElapsed; // Used to track moves/days elapsed for the in game date

  public GameData(Player player, GridAreaIcon goal, GridArea area, List<Obstacle> obstacles, List<Item> items,
      boolean running) {
    this.player = player;
    this.goal = goal;
    this.area = area;
    this.obstacles = obstacles;
    this.items = items;
    this.running = running;
    this.daysElapsed = 0;
  }

  public Player getPlayer() {
    return player;
  }

  public GridAreaIcon getGoal() {
    return goal;
  }

  public GridArea getArea() {
    return area;
  }

  public List<Obstacle> getObstacles() {
    return obstacles;
  }

  public List<Item> getItems() {
    return items;
  }

  public List<PluginListenerApi> getListeners() {
    return listeners;
  }

  public boolean getRunning() {
    return running;
  }

  public int getDaysElapsed() {
    return daysElapsed;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public void setGoal(GridAreaIcon goal) {
    this.goal = goal;
  }

  public void setArea(GridArea area) {
    this.area = area;
  }

  public void setObstacles(List<Obstacle> obstacles) {
    this.obstacles = obstacles;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public void setListeners(List<PluginListenerApi> listeners) {
    this.listeners = listeners;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public void setDaysElapsed(int daysElapsed) {
    this.daysElapsed = daysElapsed;
  }
}
