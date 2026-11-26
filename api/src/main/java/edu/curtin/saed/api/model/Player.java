package edu.curtin.saed.api.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Player {
    
    private GridAreaIcon icon;
    private ObservableList<Item> inventory = FXCollections.observableArrayList();
    private int vision;

    public Player(GridAreaIcon icon, int vision) {
        this.icon = icon;
        this.vision = vision;
    }

    public void addItemToInventory(Item item) {
        this.inventory.add(item);
    }

    public GridAreaIcon getIcon() {
        return icon;
    }
    public ObservableList<Item> getInventory() {
        return inventory;
    }
    public int getVision() {
        return vision;
    }

    public void setIcon(GridAreaIcon icon) {
        this.icon = icon;
    }
    public void setInventory(ObservableList<Item> inventory) {
        this.inventory = inventory;
    }    
    public void setVision(int vision) {
        this.vision = vision;
    }
}
