package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.shapeList;

/**
 * Created by popko on 11/12/2016.
 */
public class SpatialViewerForQueryingStore extends javax.swing.JPanel {

    public Color filling = Color.GRAY;
    public static List<CustomShape> privateShapeList = shapeList;
    public static int status;
    public static final int NONE = 0;
    public static final int SELECT_POINT = 1;
    public static final int SELECT_RACKS_TO_JOIN = 2;
    public static final int SELECT_AREA = 3;
    public static Rectangle selectedArea;

    public SpatialViewerForQueryingStore()
    {
        status = NONE;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("Clicked on point " + e.getPoint());
                if (status == SELECT_POINT)
                    drawSelectedPoint(e.getPoint());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            Point start = new Point();

            @Override
            public void mouseDragged(MouseEvent e) {
                Point end = e.getPoint();
                selectedArea = new Rectangle(start, new Dimension(end.x-start.x, end.y-start.y));
                repaint();
                //System.out.println("Rectangle: "+selectedArea);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                start = e.getPoint();
                repaint();
                //System.out.println("Start point: " + start);
            }
        });
    }

    private void drawSelectedPoint(Point clickedPoint) {

        JGeometry selectedJgeometry = new JGeometry(clickedPoint.getX()-2.5, clickedPoint.getY()-2.5 , clickedPoint.getX() + 2.5, clickedPoint.getY()+ 2.5, 0);
        selectedJgeometry = selectedJgeometry.createCircle(clickedPoint.getX(), clickedPoint.getY(), 6, 0);

        Shape shape = selectedJgeometry.createShape();
        CustomShape selectedPoint = new CustomShape(shape, -1,-1);

        CustomShape lastSelectedPoint = null;
        lastSelectedPoint = getSelectedShapeObject();
        if (lastSelectedPoint != null)
        {
            lastSelectedPoint.unSelect();
            privateShapeList.remove(lastSelectedPoint);
        }
        selectedPoint.setSelected();
        selectedPoint.setDeleteAfterAction(true);
        privateShapeList.add(selectedPoint);

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

            if (shapeObject.isSelected())
                filling = Color.RED;
            else
                filling = Color.GRAY;
            g2D.setPaint(filling);
            g2D.fill(shapeObject.getShape());
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(shapeObject.getShape());

            g2D.setTransform(old);
        }

        if (status == SELECT_AREA && selectedArea != null)
        {
            filling = Color.RED;
            g2D.setPaint(filling);
            g2D.draw(selectedArea);
            filling = new Color(255,255,255,150);
            g2D.setPaint(filling);
            g2D.fill(selectedArea);
            //System.out.println("Drowing area: " + selectedArea);
        }
        filling = Color.GRAY;
    }

    public static CustomShape getSelectedShapeObject()
    {
        for (CustomShape shapeObject : privateShapeList)
        {
            if (shapeObject.isSelected())
                return shapeObject;
        }
        return null;
    }

    public static CustomShape getShapeById(int id)
    {
        for (CustomShape shapeObject : privateShapeList)
        {
            if (shapeObject.getId() == id)
                return shapeObject;
        }
        return null;
    }

    public static void unselectAllShapes()
    {
        for (CustomShape shapeObject : privateShapeList)
        {
            shapeObject.unSelect();
        }
    }


    public static void deleteAllTemporalShapes()
    {
        for (Iterator<CustomShape> iterator = privateShapeList.iterator(); iterator.hasNext(); ) {
            CustomShape shapeObject = iterator.next();
            if (shapeObject.getDeleteAfterAction()) {
                iterator.remove();
            }
        }
    }
}
