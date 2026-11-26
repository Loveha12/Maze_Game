package edu.curtin.saed.api.controllers;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.apiinterfaces.GameLogicApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridAreaIcon;
import edu.curtin.saed.api.model.Item;
import edu.curtin.saed.api.model.Obstacle;
import edu.curtin.saed.api.model.Player;
import java.util.List;

/*
 * This file houses all of the games logic and moving parts
 */

public class GameLogic implements GameLogicApi {

    // List to hold all registered plugin listeners
    private GameDataController dataController = new GameDataController();

    @Override
    public void firePluginActionSelected(GameData data, String pluginName) {
        System.out.println("Firing " + pluginName + " plugin.");
        for (PluginListenerApi listener : data.getListeners()) {
            if (listener.getPluginName().equals(pluginName)) {
                listener.onPluginActionSelected(data);
            }
        }
    }

    private void firePlayerMove(GameData data, double newX, double newY) {
        for (PluginListenerApi listener : data.getListeners()) {
            listener.onPlayerMove(data, newX, newY);
        }
        dataController.revealSurroundingTile(data, newX, newY, data.getPlayer().getVision());
    }

    private void fireItemAcquired(GameData data, Item item) {
        for (PluginListenerApi listener : data.getListeners()) {
            listener.onItemAcquired(data, item.getItemName());
        }
    }

    private boolean isPassableObstacle(Player player, Obstacle obstacle) {
        List<String> requiredItems = obstacle.getRequiredItemNames();

        // Allows for obstacles that cannot be passed
        if (requiredItems == null || requiredItems.isEmpty()) {
            System.out.println("Obstacle is impassable");
            return false;
        }
        
        List<Item> playerInventory = player.getInventory();
        int requiredNumberOfItems = 0;
        // Check if player has all required items
        for (String requiredName : requiredItems) {
            for (Item inventoryItem : playerInventory) {
                if (inventoryItem.getItemName().equals(requiredName)) {
                    requiredNumberOfItems++;
                }
            }
        }
        if (requiredNumberOfItems == requiredItems.size()) {
            System.out.println("You were able to pass through the door with the required items");
            return true;
        } else {
            System.out.println("You're missing some required items");
        }
        // If all required items are not found
        return false;
    }


    private boolean checkAndHandleCollision(GameData data, double newX, double newY) {

        for (GridAreaIcon icon : data.getArea().getIcons()) {
            if (icon.getX() == newX && icon.getY() == newY) {
                // Check for Obstacle
                for (Obstacle obstacle : data.getObstacles()) {
                    if (obstacle.getIcon().getX() == newX && obstacle.getIcon().getY() == newY) {
                        if (isPassableObstacle(data.getPlayer(), obstacle)) {
                            data.getArea().getIcons().remove(obstacle.getIcon());
                            return true;
                        }
                        return false;
                    }
                }

                // Check for Item
                Item itemToPickUp;
                for (Item item : data.getItems()) {
                    if (item.getIcon().getX() == newX && item.getIcon().getY() == newY) {
                        itemToPickUp = item;
                        if (itemToPickUp != null) {
                            itemPickup(data, itemToPickUp);
                            // Remove item from the map (GridAreaIcon list) and the item list
                            data.getArea().getIcons().remove(itemToPickUp.getIcon());
                            data.getItems().remove(itemToPickUp);
                        }
                        return true;
                    }
                }
            }
        }

        // Move is allowed (either nothing there, or an item was picked up, or a
        // passable obstacle was encountered)
        return true;
    }

    @Override
    public void movePlayer(char direction, GameData data) {
        if (!data.getRunning()) {
            System.out.println("Game over");
            return;
        }
        Player player = data.getPlayer();

        double oldX = player.getIcon().getX();
        double oldY = player.getIcon().getY();

        double dx = 0;
        double dy = 0;

        switch (direction) {
            case 'v':
                dy = 1;
                break;
            case '^':
                dy = -1;
                break;
            case '<':
                dx = -1;
                break;
            case '>':
                dx = 1;
                break;
            default:
                return;
        }

        double newX = oldX + dx;
        double newY = oldY + dy;

        // Increment day count regardless of whether the move is successful, as the
        // player 'attempts' to move.
        data.setDaysElapsed((data.getDaysElapsed() + 1));

        // Check bounds
        if (newX >= 1 && newX < (data.getArea().getGridWidth()) &&
                newY >= 1 && newY < (data.getArea().getGridHeight())) {
            // Check for obstacles/items before setting location
            if (checkAndHandleCollision(data, newX, newY)) {
                // Move is allowed
                dataController.setPlayerLocation(data, newX, newY);
                checkWinCondition(data);
                // Fire the event after successful movement
                firePlayerMove(data, newX, newY);
            }

            

        } else {
            System.out.println("Invalid move (out of bounds).");
        }
    }

    // Called whenever you move to a coordinate with an item on it
    @Override
    public void itemPickup(GameData data, Item item) {
        data.getPlayer().getInventory().add(item);
        System.out.println("Picked up item: " + item.getItemName() + " (" + item.getDescription() + ")");
        fireItemAcquired(data, item);
    }

    @Override
    public void exitGame(GameData data) {
        data.setRunning(false);
    }

    private void checkWinCondition(GameData data) {
        if (data.getGoal().getX() == data.getPlayer().getIcon().getX() &&
                data.getGoal().getY() == data.getPlayer().getIcon().getY()) {
            System.out.println("Goal Reached!");
            data.getArea().getIcons().get(1).setShown(false);
            exitGame(data);
        }
    }
}
