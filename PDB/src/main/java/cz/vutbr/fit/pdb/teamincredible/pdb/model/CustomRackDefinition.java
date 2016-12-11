package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import java.awt.*;

/**
 * Created by popko on 10/12/2016.
 */
public class CustomRackDefinition {

    private int id;
    private Shape shape;
    private Rectangle boundingBox;
    private boolean selected;

    public CustomRackDefinition(int id, Shape shape)
    {
        this.id = id;
        this.shape = shape;
        this.boundingBox = shape.getBounds();
    }

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

    public void unSelect()
    {
        this.selected = false;
    }

    public void recalculateBoundingBox(double x, double y)
    {
        this.boundingBox.translate(((int) x), ((int) y));
    }

    public void refreshBoundingBox()
    {
        this.boundingBox = this.shape.getBounds();
    }

}
