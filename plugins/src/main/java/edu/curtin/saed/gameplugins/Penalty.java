package edu.curtin.saed.gameplugins;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridAreaIcon;
import edu.curtin.saed.api.model.Obstacle;

import java.util.Random;

/*
 * Penalty plugin, specified in the assignment requirements
 */

public class Penalty implements PluginListenerApi {

  private final String pluginName = "Penalty";
  private static final long TIME_THRESHOLD_MS = 5000;

  private volatile boolean running = false;
  private volatile long lastMoveTime = 0;
  private boolean validMove = false;

  private final Random random = new Random();

  private void checkLastMoveTime(GameData data, double x, double y) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastMoveTime > TIME_THRESHOLD_MS) {
      Obstacle penaltyObstacle = new Obstacle(null, null);
      int randomChoice = 0;
      int maxChecks = 50;
      int checkCount = 0;
      validMove = false;
      while (!validMove && checkCount < maxChecks) {
        randomChoice = random.nextInt(4) + 1;
        switch (randomChoice) {
            case 1:
                penaltyObstacle.setIcon(new GridAreaIcon((data.getPlayer().getIcon().getX()+1), data.getPlayer().getIcon().getY(), 0.0, 1.0, GameData.class.getClassLoader().getResourceAsStream("obstacle.png"), "Obstacle"));
                break;
            case 2:
                penaltyObstacle.setIcon(new GridAreaIcon((data.getPlayer().getIcon().getX()-1), data.getPlayer().getIcon().getY(), 0.0, 1.0, GameData.class.getClassLoader().getResourceAsStream("obstacle.png"), "Obstacle"));
                break;
            case 3:
                penaltyObstacle.setIcon(new GridAreaIcon(data.getPlayer().getIcon().getX(), (data.getPlayer().getIcon().getY()+1), 0.0, 1.0, GameData.class.getClassLoader().getResourceAsStream("obstacle.png"), "Obstacle"));
                break;
            case 4:
                penaltyObstacle.setIcon(new GridAreaIcon(data.getPlayer().getIcon().getX(), (data.getPlayer().getIcon().getY()-1), 0.0, 1.0, GameData.class.getClassLoader().getResourceAsStream("obstacle.png"), "Obstacle"));
                break;
            default:
                return;
        }
        checkCount++;
        if (checkValidPlacement(data, penaltyObstacle.getIcon())) {
          data.getObstacles().add(penaltyObstacle);
          data.getArea().getIcons().add(penaltyObstacle.getIcon());
          System.out.println("Penalty placed for moving too slow!");
          validMove = true;
        }
        if (!validMove && checkCount == maxChecks) {
          System.out.println("Couldn't find a valid penalty placement");
        }
      }
    }
    this.lastMoveTime = System.currentTimeMillis();
  }

  private boolean checkValidPlacement(GameData data, GridAreaIcon penaltyIcon) {
    for (GridAreaIcon icon : data.getArea().getIcons()) {
      if (icon.getX() == penaltyIcon.getX() && icon.getY() == penaltyIcon.getY()) {
        return false;
      }
    }
    if (penaltyIcon.getX() < 0 || penaltyIcon.getX() >= data.getArea().getGridWidth() || penaltyIcon.getY() < 0 || penaltyIcon.getY() >= data.getArea().getGridHeight()) {
      return false;
    } else {
      return true;
    }
    
  }

  @Override
  public void onPlayerMove(GameData data, double newX, double newY) {
    if (this.running) {
      checkLastMoveTime(data, newX, newY);
    }
  }

  @Override
  public void onPluginActionSelected(GameData data) {
    if (!this.running) {
      this.running = true;
      this.lastMoveTime = System.currentTimeMillis();
    }
    else {
      this.running = false;
      this.lastMoveTime = 0;
      System.out.println(pluginName + " shutting down...");
    }
  }

  @Override
  public void onItemAcquired(GameData data, String itemName) {
    // Not used by this plugin
  }

  @Override
  public String getPluginName() {
    return pluginName;
  }

}