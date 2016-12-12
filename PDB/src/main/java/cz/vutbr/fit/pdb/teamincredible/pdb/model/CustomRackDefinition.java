package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import java.awt.*;

/**
 * Created by popko on 10/12/2016
 * Custom class for storing and manipulation with racks
 */
public class CustomRackDefinition {

    private int id;
    private Shape shape;
    private Rectangle boundingBox;
    private boolean selected;

    /**
     * Constructor for the custom rack definition
     * @param id int identification of the rack
     * @param shape Shape shape of the rack
     */
    public CustomRackDefinition(int id, Shape shape)
    {
        this.id = id;
        this.shape = shape;
        this.boundingBox = shape.getBounds();
    }


    /**
     * Unused constructor
     * @param id int identification of the rack
     */
    public CustomRackDefinition(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public Rectangle getBoundingBox()
    {
        return this.boundingBox;
    }

    public Shape getShape()
    {
        return this.shape;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected()
    {
        this.selected = true;
    }

    /**
     * Remove selection (pretty much inverse setter)
     */
    public void unSelect()
    {
        this.selected = false;
    }

    /**
     * Recalculates bounding box for the rack
     * @param x double x coord
     * @param y double y coord
     */
    public void recalculateBoundingBox(double x, double y)
    {
        this.boundingBox.translate(((int) x), ((int) y));
    }


    /**
     * Refreshes bounding box for the rack
     */
    public void refreshBoundingBox()
    {
        this.boundingBox = this.shape.getBounds();
    }

}
