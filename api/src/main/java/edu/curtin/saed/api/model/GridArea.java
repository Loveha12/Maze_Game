package edu.curtin.saed.api.model;

import javafx.geometry.VPos;
import javafx.scene.canvas.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import java.util.*;

public class GridArea extends Pane
{
    private double gridWidth;
    private double gridHeight;
    private double gridSquareSize = 1.0; // Re-calculated
    private boolean gridLines = true;
    private Color captionColour = Color.GREEN;
    private List<GridAreaIcon> icons = new ArrayList<>();
    private Canvas canvas = null;
    
    // Map to store custom colors for specific grid squares.
    // Key: "x,y" (e.g., "1,5"), Value: Color
    private final Map<String, Color> squareColors = new HashMap<>();

    public GridArea(double gridWidth, double gridHeight)
    {
        // Initializing with +1 assumes grid dimensions start from 1,1 up to width,height
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public void setGridLines(boolean gridLines)
    {
        this.gridLines = gridLines;
    }

    public double getGridWidth()
    {
        return gridWidth;
    }
    public double getGridHeight()
    {
        return gridHeight;
    }
    public void setGridSize(double gridWidth, double gridHeight)
    {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    /**
     * Retrieves a modifiable list of GridAreaIcons. Use this to _add_ new icon objects; e.g.:
     *
     * GridArea area = ...;
     * area.getIcons().add(new GridAreaIcon(...));
     * area.requestLayout();
     */
    public List<GridAreaIcon> getIcons()
    {
        return icons;
    }

    /**
     * Sets the colour used to display the caption text for each icon.
     */
    public void setCaptionColour(Color captionColour)
    {
        this.captionColour = captionColour;
    }

    /*
     * Sets the background color of a specific grid square (x, y). 
     */
    public void setSquareColorInternal(double x, double y, Color color) {
        String key = (int)x + "," + (int)y; 
        
        if (color == null || color.equals(Color.TRANSPARENT)) {
            squareColors.remove(key);
        } else {
            squareColors.put(key, color);
        }
        requestLayout();
    }
    
    /**
     * Sets the visibility state of a specific grid square (x, y). 
     */
    public void setSquareColor(double x, double y, boolean visible) {
        Color color = visible ? Color.LIGHTGRAY : Color.BLACK;
        setSquareColorInternal(x, y, color); 

        int targetX = (int)x;
        int targetY = (int)y;

        for (GridAreaIcon icon : icons) {
            if ((int)icon.getX() == targetX && (int)icon.getY() == targetY) {
                icon.setShown(visible);
            }
        }
    }
    
    /**
     * Helper to retrieve the current custom color map (mostly for debugging).
     */
    public Map<String, Color> getSquareColors() {
        return squareColors;
    }


    /**
     * Redraws the grid area, either because the user is manipulating the window, OR because you've
     * called 'requestLayout()'.
     */
    @Override
    public void layoutChildren()
    {
        super.layoutChildren();
        if(canvas == null)
        {
            canvas = new Canvas();
            canvas.widthProperty().bind(widthProperty());
            canvas.heightProperty().bind(heightProperty());
            getChildren().add(canvas);
        }

        GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        // First, calculate how big each grid cell should be, in pixels.
        gridSquareSize = Math.min(getWidth() / gridWidth,
                                  getHeight() / gridHeight);

        for (Map.Entry<String, Color> entry : squareColors.entrySet()) {
            String[] coords = entry.getKey().split(",");
            try {
                int gridX = Integer.parseInt(coords[0]);
                int gridY = Integer.parseInt(coords[1]);
                Color color = entry.getValue();

                // Calculate the top-left pixel position for the square
                double x = (gridX - 0.5) * gridSquareSize; 
                double y = (gridY - 0.5) * gridSquareSize;

                gfx.setFill(color);
                gfx.fillRect(x, y, gridSquareSize, gridSquareSize);
            } catch (NumberFormatException e) {
                // Log error if a malformed key is found, but continue drawing
                System.err.println("Invalid coordinate key in squareColors map: " + entry.getKey());
            }
        }
        
        if(gridLines)
        {
            // Draw the arena grid lines.
            gfx.setStroke(Color.BLACK);

            for(double gridX = 0.0; gridX < gridWidth; gridX++) // Internal vertical grid lines
            {
                double x = (gridX + 0.5) * gridSquareSize;
                gfx.strokeLine(x, gridSquareSize / 2.0, x, (gridHeight - 0.5) * gridSquareSize);
            }

            for(double gridY = 0.0; gridY < gridHeight; gridY++) // Internal horizontal grid lines
            {
                double y = (gridY + 0.5) * gridSquareSize;
                gfx.strokeLine(gridSquareSize / 2.0, y, (gridWidth - 0.5) * gridSquareSize, y);
            }
        }

        // Draw all the images and their captions.
        for(var icon : icons)
        {
            if(icon.isShown())
            {
                drawIcon(gfx, icon);
            }
        }
    }

    /**
     * Draw a GridAreaIcon -- its image and caption -- at their proper location. Only
     * to be called from within layoutChildren().
     */
    private void drawIcon(GraphicsContext gfx, GridAreaIcon icon)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn.
        double x = (icon.getX()) * gridSquareSize;
        double y = (icon.getY()) * gridSquareSize;

        // We also need to know how "big" to make the image.
        var image = icon.getImage();
        double fullSizePixelWidth = image.getWidth();
        double fullSizePixelHeight = image.getHeight();

        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Image is wider than it is high.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Image is taller than it is wide.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.save();
        gfx.translate(x, y);
        gfx.rotate(icon.getRotation());
        gfx.drawImage(
            image,
            -displayedPixelWidth / 2.0,
            -displayedPixelHeight / 2.0,
            displayedPixelWidth,
            displayedPixelHeight);
        gfx.restore();

        // Draw the caption below the image.
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(captionColour);
        gfx.strokeText(icon.getCaption(), x, y + (gridSquareSize / 4.0));
    }
}
