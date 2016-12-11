package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.shapeList;

/**
 * Created by popko on 11/12/2016.
 */
public class SpatialViewerForQueryingStore extends javax.swing.JPanel {

    public Color filling = Color.DARK_GRAY;
    private static final List<CustomShape> privateShapeList = shapeList;

    public void SpatialViewerForStore()
    {

    }

    /**
     * Invoked by Swing to draw components. Applications should not invoke paint
     * directly, but should instead use the repaint method to schedule the
     * component for redrawing.
     *
     * @param g the Graphics context in which to paint
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        // draw the Shape objects
        for (CustomShape shapeObject : privateShapeList) {

            if (shapeObject.isDeleted())
                continue;

            g2D.setPaint(filling);

            int rotation = shapeObject.getRotation();
            AffineTransform old = g2D.getTransform();

            while (rotation > 0)
            {
                //System.out.println("Rotating shape: "+ shapeObject.getShape().toString());
                g2D.rotate(Math.toRadians(45), shapeObject.getBoundingBox().x + shapeObject.getBoundingBox().width/3, shapeObject.getBoundingBox().y + shapeObject.getBoundingBox().height/4);
                rotation--;
            }

            Point translationPoint = shapeObject.getTranslation();
            g2D.translate(translationPoint.x, translationPoint.y);

            g2D.setPaint(filling);
            g2D.fill(shapeObject.getShape());
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(shapeObject.getShape());

            g2D.setTransform(old);
        }
    }
}
