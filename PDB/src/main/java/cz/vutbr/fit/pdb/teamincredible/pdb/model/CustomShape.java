package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import java.awt.*;

import static cz.vutbr.fit.pdb.teamincredible.pdb.MainApp.UNIT;

/**
 * Created by popko on 07/12/2016.
 */
public class CustomShape {

    private Shape shape;
    private int rotation;
    private Point translation;
    private Rectangle boundingBox;
    private boolean selected;
    private int rackTypeId;
    private int id;
    private boolean deleted;

    public CustomShape(Shape shape, int rackTypeId, int id)
    {
        this.shape = shape;
        this.rotation = 0;
        this.boundingBox = shape.getBounds();
        this.boundingBox.width = 3*UNIT;
        this.boundingBox.height = 4*UNIT;
        this.translation = new Point(0,0);
        this.selected = false;
        this.rackTypeId = rackTypeId;
        this.id  = id;
        deleted = false;
    }

    public Shape getShape()
    {
        return this.shape;
    }

    public Point getTranslation()
    {
        return this.translation;
    }

    public int getRotation()
    {
        return this.rotation;
    }

    public Rectangle getBoundingBox()
    {
        return this.boundingBox;
    }

    public int getId() {
        return id;
    }

    public int getRackTypeId() {
        return rackTypeId;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setShape(Shape shape)
    {
        this.shape = shape;
    }

    public void setTranslation(Point translation)
    {
        this.translation = translation;
    }

    public void setRotation(int rotation)
    {
        this.rotation = rotation;
    }

    public void incrementRotation()
    {
        this.rotation = (this.rotation + 1) % 8;
    }

    public void setBoundingBox(Rectangle boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public void recalculateBoundingBox(double x, double y)
    {
        this.boundingBox.translate(((int) x), ((int) y));
    }

    public void setSelected()
    {
        this.selected = true;
    }

    public void unSelect()
    {
        this.selected = false;
    }

    public boolean moveDown()
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.y + this.boundingBox.height + UNIT >= 500)
            return false;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x, this.translation.y + UNIT));
        this.recalculateBoundingBox(0, UNIT);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
        return true;
    }

    public boolean moveUp()
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.y - UNIT <= 0)
            return false;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x, this.translation.y - UNIT));
        this.recalculateBoundingBox(0, 0-UNIT);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
        return true;
    }

    public boolean moveLeft()
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.x - UNIT <= 0)
            return false;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x - UNIT, this.translation.y));
        this.recalculateBoundingBox(0 - UNIT, 0);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
        return true;
    }

    public boolean moveRight()
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.x + this.boundingBox.width + UNIT >= 500)
            return false;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x + UNIT, this.translation.y));
        this.recalculateBoundingBox(UNIT, 0);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());

        return true;
    }

    public void delete()
    {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
