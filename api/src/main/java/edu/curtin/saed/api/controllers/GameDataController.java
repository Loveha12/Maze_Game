package edu.curtin.saed.api.controllers;

import java.util.List;

import edu.curtin.saed.api.apiinterfaces.GameDataApi;
import edu.curtin.saed.api.apiinterfaces.PluginListenerApi;
import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridArea;
import edu.curtin.saed.api.model.GridAreaIcon;
import edu.curtin.saed.api.model.Item;
import edu.curtin.saed.api.model.Player;
import javafx.collections.ObservableList;

/*
 * This file is build specifically to interact with and change the main GameData block
 * It does not cover every function within GameData but it does have everything required
 * to cover the assignment requirements.
 */

public class GameDataController implements GameDataApi {

  @Override
  public void registerListener(GameData data, PluginListenerApi listener) {
    data.getListeners().add(listener);
  }

  @Override
  public void setPlayerLocation(GameData data, double x, double y) {
    data.getPlayer().getIcon().setPosition(x, y);
  }

  @Override
  public void setPlayerInventory(Player player, ObservableList<Item> inventory) {
    player.setInventory(inventory);
  }

  @Override
  public void setGridSize(GridArea area, double x, double y) {
    area.setGridSize(x, y);
  }

  @Override
  public GridAreaIcon getSquareContents(List<GridAreaIcon> iconList, double x, double y) {
    for (GridAreaIcon icon : iconList) {
      if (icon.getX() == x && icon.getY() == y) {
        return icon;
      }
    }
    return null;
  }

  @Override
  public void modifySquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon) {
    for (int i = 0; i < iconList.size(); i++) {
      GridAreaIcon currentIcon = iconList.get(i);
      if (currentIcon.getX() == x && currentIcon.getY() == y) {
        iconList.set(i, icon);
        return;
      }
    }
  }

  @Override
  public void addSquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon) {
    iconList.add(icon);
  }

  @Override
  public void removeSquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon) {
    iconList.removeIf(currentIcon -> currentIcon.getX() == x && currentIcon.getY() == y);
  }

  @Override
    public void setSquareVisibility(GameData data, double x, double y, boolean visible) {
      data.getArea().setSquareColor(x, y, visible);
    }

    @Override
    public void revealSurroundingTile(GameData data, double newX, double newY) {
        this.setSquareVisibility(data, newX, newY, true);
        this.setSquareVisibility(data, (newX+1), newY, true);
        this.setSquareVisibility(data, (newX-1), newY, true);
        this.setSquareVisibility(data, newX, (newY+1), true);
        this.setSquareVisibility(data, newX, (newY-1), true);
    }
}
