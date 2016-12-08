package cz.vutbr.fit.pdb.teamincredible.pdb;

import java.awt.*;

/**
 * Created by popko on 07/12/2016.
 */
public class CustomShape {

    private Shape shape;
    private int rotation;
    private Point translation;
    private Rectangle boundingBox;
    private boolean selected;

    public CustomShape(Shape shape)
    {
        this.shape = shape;
        this.rotation = 0;
        this.boundingBox = shape.getBounds();
        this.boundingBox.width = 15;
        this.boundingBox.height = 20;
        this.translation = new Point(0,0);
        this.selected = false;
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

    public void unselect()
    {
        this.selected = false;
    }

    public void moveDown(int unit)
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.y + this.boundingBox.height + unit >= 500)
            return;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x, this.translation.y + unit));
        this.recalculateBoundingBox(0, unit);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
    }

    public void moveUp(int unit)
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.y - unit <= 0)
            return;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x, this.translation.y - unit));
        this.recalculateBoundingBox(0, 0-unit);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
    }

    public void moveLeft(int unit)
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.x - unit <= 0)
            return;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x - unit, this.translation.y));
        this.recalculateBoundingBox(0 - unit, 0);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
    }

    public void moveRight(int unit)
    {
        //can we move down?
        //I am still in the canvas?
        if (this.boundingBox.x + this.boundingBox.width + unit >= 500)
            return;

        System.out.println("Bounding box: "+ this.boundingBox.toString());
        System.out.println("Translation: "+ this.translation.toString());

        this.setTranslation(new Point(this.translation.x + unit, this.translation.y));
        this.recalculateBoundingBox(unit, 0);
        System.out.println("Recalculated bounding box: "+ this.boundingBox.toString());
        System.out.println("Recalculated translation: "+ this.translation.toString());
    }
}
