package edu.curtin.saed.gameplugins;

import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.controllers.GameDataController;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.Player;

import java.util.Random;

/*
 * Teleport plugin, specified in the assignment requirements
 */

public class Teleport implements PluginListenerApi {
    
    private String pluginName = "Teleport";
    private boolean hasUsedTeleport = false;
    private final Random random = new Random();
    private GameDataController dataController = new GameDataController();
    
    public String getPluginName() {
        return pluginName;
    }
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public void onPluginActionSelected(GameData data) {
        if (!hasUsedTeleport) {
            
            double randomCellX = 0;
            double randomCellY = 0;
            double newCenteredX = 0;
            double newCenteredY = 0;
            boolean validMove = false;

            while (!validMove) {
                randomCellX = random.nextDouble(data.getArea().getGridWidth()); // rounds the value down to nearest whole number
                randomCellY = random.nextDouble(data.getArea().getGridHeight());
                randomCellX = Math.round(randomCellX);
                randomCellY = Math.round(randomCellY);
                if (randomCellX >= 1 && randomCellX < (data.getArea().getGridWidth() ) &&
                    randomCellY >= 1 && randomCellY < (data.getArea().getGridHeight() )) {
                    validMove = true;
                    newCenteredX = randomCellX;
                    newCenteredY = randomCellY;
                }
            }
            
            Player player = data.getPlayer();
            player.getIcon().setPosition(newCenteredX, newCenteredY);

            data.getArea().getIcons().set(0, player.getIcon());
            onPlayerMove(data, newCenteredX, newCenteredY);
            this.hasUsedTeleport = true;
            
            System.out.println("TELEPORT: Success! Player warped to (" + (newCenteredX) + ", " + (newCenteredY) + "). Use consumed.");
        } else {
            System.out.println("TELEPORT: Failed. Player has already used the teleport function.");
        }
    }

    @Override 
    public void onPlayerMove(GameData data, double newX, double newY) {
        dataController.revealSurroundingTile(data, newX, newY, data.getPlayer().getVision());
    }
    @Override public void onItemAcquired(GameData data, String itemName) { /* Not used by this plugin */ }
}

