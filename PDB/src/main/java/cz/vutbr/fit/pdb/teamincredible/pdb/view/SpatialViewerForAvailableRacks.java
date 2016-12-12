package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.SpatialConverters;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.leftButton;

/**
 * Created by Anna
 * File contains viewer for dialog for selecting rack to insert into the store
 */
public class SpatialViewerForAvailableRacks extends javax.swing.JPanel {

    public Color filling;
    public static List<CustomRackDefinition> racksList;

    /**
     * Costructor loads rack definitions from database and initializes listeners for selecting inserted rack type.
     * @throws SQLException
     */
    public SpatialViewerForAvailableRacks() throws SQLException {

        racksList = new ArrayList<>();
        filling = Color.gray;

        // load the Shape objects from a db.
        try {
            getAvailableRacks();

        } catch (Exception e) {
            e.printStackTrace();
        }

        racksList.sort(Comparator.comparing(CustomRackDefinition::getId));


        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                switch (e.getButton()) {
                    case leftButton:
                        selectOrUnselectShape(e.getPoint());
                        break;
                }
            }
        });

    }

    /**
     * Checks position of mouseclick and selectes rack which is situated in the clicked position
     * @param point
     */
    private void selectOrUnselectShape(Point point) {

        System.out.println();
        unselectAllRacks();
        int i = 0;
        for (CustomRackDefinition shapeObject : racksList) {

            if (shapeObject.getBoundingBox().contains(point))
            {
                shapeObject.setSelected();
                i++;
            }
        }
        if (i != 1)
        {
            unselectAllRacks();
        }
        repaint();
    }

    /**
     * LoLoads rack definitions from the database
     * @throws SQLException
     */
    private void getAvailableRacks() throws SQLException  {
        // create a OracleDataSource instance

        try (Connection connection = DatabaseD.getConnection())
        {
            try (
                    PreparedStatement statement = connection.prepareStatement("select * from rack_definitions");
                    ResultSet resultSet = statement.executeQuery();
            )
            {
                while (resultSet.next()) {
                    // get a JGeometry object (the Java representation of SDO_GEOMETRY data)
                    byte[] image = resultSet.getBytes("rack_defs_shape");
                    int id = resultSet.getInt("rack_defs_id");

                    JGeometry jGeometry = JGeometry.load(image);

                    // get a Shape object (the object drawable into Java GUI)
                    Shape shape = SpatialConverters.jGeometry2Shape(jGeometry);
                    CustomRackDefinition shapeObject = new CustomRackDefinition(id, shape);

                    // add the Shape object into a list of drawable objects
                    if (shape != null) {
                        racksList.add(shapeObject);
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
        // a canvas of the Graphics context
        Graphics2D g2D = (Graphics2D) g;

        if (racksList.isEmpty())
        {
            Shape testShape = new Ellipse2D.Float(200, 200, 100, 100);
            g2D.setPaint(filling);
            g2D.fill(testShape);
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(testShape);
            return;
        }
        AffineTransform old = g2D.getTransform();

        int i = 0;
        int translation = 0;
        // draw the Shape objects
        for (CustomRackDefinition shapeObject : racksList) {
            //System.out.println("Drawing shape: "+ shapeObject.getShape().toString());
            // draw an interior of the shape
            g2D.setPaint(filling);


            filling = Color.blue;
            if (shapeObject.isSelected())
            {
                filling = Color.pink;
            }

            g2D.setPaint(filling);
            g2D.fill(shapeObject.getShape());
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(shapeObject.getShape());

            shapeObject.refreshBoundingBox();
            shapeObject.recalculateBoundingBox(translation, 0);

            translation += shapeObject.getBoundingBox().width + 10;

            g2D.translate(shapeObject.getBoundingBox().width + 10,0);

            i++;
        }
        g2D.setTransform(old);
    }


    /**
     * Remove selection from the selected rack type
     */
    public void unselectAllRacks()
    {
        for (CustomRackDefinition shapeObject : racksList)
        {
            shapeObject.unSelect();
        }
    }

    /**
     *
     * @return selected rack definition
     */
    public static CustomRackDefinition getSelectedRackDefinition()
    {
        for (CustomRackDefinition shapeObject : racksList)
        {
            if (shapeObject.isSelected())
                return shapeObject;
        }
        return null;
    }

}
