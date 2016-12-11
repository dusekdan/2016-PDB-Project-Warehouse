package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.SpatialConverters;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.MainApp.UNIT;
import static java.awt.event.MouseEvent.*;

/**
 * Created by popko on 10/12/2016.
 */
public class SpatialViewerForStore extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final short maxX = 800;
    private static final short maxY = 800;
    private static final short windowZoom = 1;
    public Color filling;
    public static final int leftButton = BUTTON1;
    public static final int rightButton = BUTTON3;
    public static List<CustomShape> shapeList = new ArrayList<>();

    public static final int KEY_UP = 38;
    public static final int KEY_DOWN = 40;
    public static final int KEY_LEFT = 37;
    public static final int KEY_RIGHT = 39;
    public static final int KEY_ENTER = 10;
    public static final int KEY_DELETE = 127;
    public static boolean hasChanges;



    public SpatialViewerForStore() throws SQLException {
        filling = Color.ORANGE;
        System.out.println("Calling Ex2SpatialViewer constructor ...");
        // load the Shape objects from a db.
        try {
            getExistingRacks(shapeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        shapeList.sort(Comparator.comparing(CustomShape::getId));
        hasChanges = true;

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            private void moveSelectedShape(int keyCode) {

                CustomShape shape = getSelectedShapeObject();
                if (shape == null)
                    return;
                switch (keyCode)
                {
                    case KEY_DOWN:
                        if (shape.moveDown())
                            hasChanges = true;
                        repaint();
                        break;
                    case KEY_UP:
                        if (shape.moveUp())
                            hasChanges = true;
                        repaint();
                        break;
                    case KEY_LEFT:
                        if (shape.moveLeft())
                            hasChanges = true;;
                        repaint();
                        break;
                    case KEY_RIGHT:
                        if (shape.moveRight())
                            hasChanges = true;;
                        repaint();
                        break;
                }

            }


            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode())
                {
                    case KEY_DOWN:
                    case KEY_UP:
                    case KEY_LEFT:
                    case KEY_RIGHT:
                        System.out.print("Moving key pressed...");
                        if (isFreeWay(e.getKeyCode()))
                            moveSelectedShape(e.getKeyCode());
                        break;
                    case KEY_ENTER:
                        System.out.print("Key ENTER pressed...");
                        unselectAllShapes();
                        repaint();
                        break;
                    case KEY_DELETE:
                        deleteShape();
                        repaint();
                        break;
                    default:
                        System.out.print("Key *"+ e.getKeyCode() +"* pressed...");
                        break;
                }

            }

            private boolean isFreeWay(int keyCode) {

                CustomShape selectedShape = getSelectedShapeObject();
                System.out.println("isFreeWay start... Bounding box: "+selectedShape.getBoundingBox().toString());
                if (selectedShape == null)
                {
                    System.out.println("No selected shape to move.");
                    return false;
                }
                boolean isFreeWay = true;
                Rectangle futureBoundingBox = selectedShape.getBoundingBox();
                Point translationPoint = new Point();
                switch (keyCode)
                {
                    case KEY_DOWN:
                        translationPoint = new Point(0, UNIT);
                        break;
                    case KEY_UP:
                        translationPoint = new Point(0, 0 - UNIT);
                        break;
                    case KEY_RIGHT:
                        translationPoint = new Point(UNIT, 0);
                        break;
                    case KEY_LEFT:
                        translationPoint = new Point(0 - UNIT, 0);
                        break;
                }
                futureBoundingBox.translate(translationPoint.x, translationPoint.y);

                System.out.println("isFreeWay start... Bounding box: "+selectedShape.getBoundingBox().toString());
                for (CustomShape objectShape : shapeList) {

                    if (objectShape.equals(selectedShape))
                        continue;
                    if (objectShape.getBoundingBox().intersects(futureBoundingBox))
                    {
                        futureBoundingBox.translate(0 - translationPoint.x, 0 - translationPoint.y);
                        if (!(objectShape.getBoundingBox().intersects(futureBoundingBox)))
                        {
                            futureBoundingBox.translate(translationPoint.x, translationPoint.y);
                            System.out.println("Bounding box: "+objectShape.getBoundingBox().toString() + " intersects: " + futureBoundingBox);
                            isFreeWay = false;
                        }

                    }
                }
                futureBoundingBox.translate(0 - translationPoint.x, 0 - translationPoint.y);
                return isFreeWay;
            }
        });

        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {

            private Color background;

            @Override
            public void mousePressed(MouseEvent e) {
                filling = Color.orange;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                filling = Color.gray;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton())
                {
                    case leftButton:
                        System.out.println("Mouse clicked: LEFT button ");
                        System.out.print("Clicked on point: " + e.getPoint().toString());
                        selectOrUnselectShape(e.getPoint());
                        break;
                    case BUTTON2:
                        System.out.println("Mouse clicked: BUTTON2");
                        break;
                    case rightButton:
                        System.out.println("Mouse clicked: RIGHT button ");
                        //here comes the rotation
                        System.out.print("Clicked on point: " + e.getPoint().toString());
                        try {
                            rotateRackOnPoint(e.getPoint());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                        break;
                    case NOBUTTON:
                        System.out.println("Mouse clicked: NOBUTTON");
                        break;
                }
            }

            private void selectOrUnselectShape(Point clickedPoint) {
                System.out.println();
                unselectAllShapes();
                int i = 0;
                for (CustomShape shapeObject : shapeList) {

                    System.out.println("... bounding box: " + shapeObject.getBoundingBox().toString());
                    if (shapeObject.getBoundingBox().contains(clickedPoint))
                    {
                        System.out.println("Selecting shape...");
                        shapeObject.setSelected();
                        i++;
                    }
                }
                if (i == 0)
                {
                    unselectAllShapes();
                    System.out.println("Unselecting all shapes");
                }
                //if (i != 1)
                //{
                //    unselectAllShapes();
                //    System.out.println("UnselectingShape");
                //}
                repaint();
            }

            private void rotateRackOnPoint(Point clickedPoint) throws SQLException {

                System.out.println();
                System.out.println("Trying to rotate shape...");
                for (CustomShape shapeObject : shapeList) {

                    System.out.println("... bounding box: " + shapeObject.getBoundingBox().toString());
                    if (shapeObject.getBoundingBox().contains(clickedPoint))
                    {
                        System.out.println("Trying to rotate shape...  yes: "+ shapeObject.getShape().toString());
                        rotateShape(shapeObject);
                    }
                }

            }

            private void rotateShape(CustomShape shape) {

                //rotace může být osmkrát
                System.out.println("Rotation before: "+ shape.getRotation());
                shape.incrementRotation();
                System.out.println("Rotation after increment: "+ shape.getRotation());
                hasChanges = true;
                repaint();
            }
        });

        System.out.println("Constructor of Ex2SpatialViewer ended ...");
    }

    private void deleteShape() {

        CustomShape deletedShape = getSelectedShapeObject();
        if (deletedShape != null)
        {
            deletedShape.delete();
        }
    }

    private void getExistingRacks(java.util.List<CustomShape> shapeList) throws SQLException {

        // create a OracleDataSource instance
        shapeList.clear();
        try (Connection connection = DatabaseD.getConnection())
        {
            try (
                    PreparedStatement statement = connection.prepareStatement("select * from RACKS");
                    ResultSet resultSet = statement.executeQuery();
            )
            {
                while (resultSet.next()) {
                    // get a JGeometry object (the Java representation of SDO_GEOMETRY data)
                    byte[] image = resultSet.getBytes("racks_geometry");
                    int rackId = resultSet.getInt("racks_id");
                    int rackTypeId = resultSet.getInt("racks_type");

                    JGeometry jGeometry = JGeometry.load(image);

                    System.out.println("Loading shape from database ... shape: "+ jGeometry.toString());

                    // get a Shape object (the object drawable into Java GUI)
                    Shape shape = SpatialConverters.jGeometry2Shape(jGeometry);
                    System.out.println("...Loading from database ... getBounds() of shape x: "+ shape.getBounds().x + " y: " +  shape.getBounds().y);
                    CustomShape shapeObject = new CustomShape(shape, rackTypeId, rackId);
                    System.out.println("...Loading from database ... boundingBox() of shape: "+ shapeObject.getBoundingBox().toString());

                    // add the Shape object into a list of drawable objects
                    if (shape != null) {
                        shapeList.add(shapeObject);
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
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
        // a list of Shape objects to draw
        //System.out.println("Calling paint()...");
        // a canvas of the Graphics context
        Graphics2D g2D = (Graphics2D) g;


        // draw the Shape objects
        for (CustomShape shapeObject : shapeList) {
            //System.out.println("Drawing shape: "+ shapeObject.getShape().toString());
            // draw an interior of the shape
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

            System.out.println("Translating from: [" + shapeObject.getBoundingBox().x + ", " + shapeObject.getBoundingBox().x + "]");
            Point translationPoint = shapeObject.getTranslation();
            System.out.println("Translating to: "+shapeObject.getTranslation().toString());
            g2D.translate(translationPoint.x, translationPoint.y);

            filling = Color.ORANGE;
            if (shapeObject.isSelected())
            {
                filling = Color.RED;
            }

            g2D.setPaint(filling);
            g2D.fill(shapeObject.getShape());
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(shapeObject.getShape());

            //shape.getKey().getBounds().setLocation(20,30);
            g2D.setTransform(old);
        }
    }

    public void unselectAllShapes()
    {
        for (CustomShape shapeObject : shapeList)
        {
            shapeObject.unSelect();
        }
    }

    public CustomShape getSelectedShapeObject()
    {
        for (CustomShape shapeObject : shapeList)
        {
            if (shapeObject.isSelected())
                return shapeObject;
        }
        return null;
    }

    public static boolean hasChanges()
    {
        return hasChanges;
    }

    public static List<CustomShape> returnAllRacksFromStore()
    {
        return shapeList;
    }


}
