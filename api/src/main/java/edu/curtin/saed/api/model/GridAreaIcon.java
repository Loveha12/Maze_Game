package edu.curtin.saed.api.model;

import java.io.InputStream;
import javafx.scene.image.Image;


public class GridAreaIcon
{
    private double x;
    private double y;
    private double rotation;
    private double scale;
    private Image image;
    private String caption;
    private boolean shown = true;

    public GridAreaIcon(double x, double y, double rotation, double scale, Image image, String caption)
    {
        this.x = (x); // adjusted for 0 values in input file
        this.y = (y); // adjusted for 0 values in input file
        this.rotation = rotation;
        this.scale = scale;
        this.image = image;
        this.caption = caption;
    }

    public GridAreaIcon(double x, double y, double rotation, double scale, InputStream imageStream, String caption)
    {
        this(x, y, rotation, scale, new Image(imageStream), caption);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getRotation()
    {
        return rotation;
    }

    public double getScale()
    {
        return scale;
    }

    public Image getImage()
    {
        return image;
    }

    public String getCaption()
    {
        return caption;
    }

    public boolean isShown()
    {
        return shown;
    }

    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public void setShown(boolean shown)
    {
        this.shown = shown;
    }
}
