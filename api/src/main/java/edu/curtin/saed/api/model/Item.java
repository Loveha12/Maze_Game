package edu.curtin.saed.api.model;



public class Item {

    private String itemName;
    private GridAreaIcon icon;
    private String description;

    public Item(String itemName, GridAreaIcon icon, String description) {
        this.itemName = itemName;
        this.icon = icon;
        this.description = description;
    }

    public String getItemName() {
        return itemName;
    }

    public GridAreaIcon getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setIcon(GridAreaIcon icon) {
        this.icon = icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
