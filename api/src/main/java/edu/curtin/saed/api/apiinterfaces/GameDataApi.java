package edu.curtin.saed.api.apiinterfaces;

import java.util.List;

import edu.curtin.saed.api.model.GameData;
import edu.curtin.saed.api.model.GridArea;
import edu.curtin.saed.api.model.GridAreaIcon;
import edu.curtin.saed.api.model.Item;
import edu.curtin.saed.api.model.Player;
import javafx.collections.ObservableList;

/*
 * Api used by the GameDataController to modify all data within the 
 * GameData variable
 */

public interface GameDataApi {

  void registerListener(GameData data, PluginListenerApi listener);
  void setPlayerLocation(GameData data, double x, double y);
  void setPlayerInventory(Player player, ObservableList<Item> inventory);
  void setGridSize(GridArea area, double x, double y);
  GridAreaIcon getSquareContents(List<GridAreaIcon> iconList, double x, double y);
  void modifySquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon);
  void addSquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon);
  void removeSquareContents(List<GridAreaIcon> iconList, double x, double y, GridAreaIcon icon);
  void setSquareVisibility(GameData data,double x, double y, boolean visible);
  void revealSurroundingTile(GameData data, double newX, double newY);

}
