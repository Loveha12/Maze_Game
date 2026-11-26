package edu.curtin.saed.api.model;

import java.util.List;

public class Obstacle {

    private GridAreaIcon icon;
    private List<String> requiredItemNames;

    public Obstacle(GridAreaIcon icon, List<String> requiredItemNames) {
        this.icon = icon;
        this.requiredItemNames = requiredItemNames;
    }

    public GridAreaIcon getIcon() {
        return icon;
    }

    public List<String> getRequiredItemNames() {
        return requiredItemNames;
    }

    public void setIcon(GridAreaIcon icon) {
        this.icon = icon;
    }

    public void setRequiredItemNames(List<String> requiredItemNames) {
        this.requiredItemNames = requiredItemNames;
    }
}
