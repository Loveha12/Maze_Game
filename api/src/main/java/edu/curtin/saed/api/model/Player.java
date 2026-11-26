package edu.curtin.saed.api.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Player {
    
    private GridAreaIcon icon;
    private ObservableList<Item> inventory = FXCollections.observableArrayList();

    public Player(GridAreaIcon icon) {
        this.icon = icon;
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

    public void setIcon(GridAreaIcon icon) {
        this.icon = icon;
    }
    public void setInventory(ObservableList<Item> inventory) {
        this.inventory = inventory;
    }
}
