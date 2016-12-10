package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.SpatialConverters;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by popko on 10/12/2016.
 */
public class SpatialViewerForAvailableRacks extends javax.swing.JPanel {

    public Color filling;
    public List<CustomRackDefinition> racksList;

    public SpatialViewerForAvailableRacks() throws SQLException {

        racksList = new ArrayList<>();
        filling = Color.gray;

        System.out.println("Calling SpatialViewerForAvailableRacks constructor ...");
        // load the Shape objects from a db.
        try {
            getAvailableRacks();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Loading from database finished... Rack list: "+ racksList.toString());

    }

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

                    System.out.println("Loading shape from database ... ");
                    System.out.println("... shape: "+ image.toString());
                    System.out.println("... id: "+ id);

                    // get a Shape object (the object drawable into Java GUI)
                    Shape shape = SpatialConverters.jGeometry2Shape(jGeometry);
                    CustomRackDefinition shapeObject = new CustomRackDefinition(id, shape);

                    // add the Shape object into a list of drawable objects
                    if (shape != null) {
                        racksList.add(shapeObject);
                        System.out.println("Adding shape to rackList... "+ shape.toString());
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
        System.out.println("Calling paint()...");
        // a canvas of the Graphics context
        Graphics2D g2D = (Graphics2D) g;

        if (racksList.isEmpty())
        {
            Shape testShape = new Ellipse2D.Float(200, 200, 100, 100);
            System.out.println("Drawing ellipse ...");
            g2D.setPaint(filling);
            g2D.fill(testShape);
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(testShape);
            return;
        }


        System.out.println("Rack list: "+ racksList.toString());

        // draw the Shape objects
        for (CustomRackDefinition shapeObject : racksList) {
            //System.out.println("Drawing shape: "+ shapeObject.getShape().toString());
            // draw an interior of the shape
            g2D.setPaint(filling);

            AffineTransform old = g2D.getTransform();

            filling = Color.blue;
            if (shapeObject.isSelected())
            {
                filling = Color.pink;
            }

            System.out.println("Drawing existing rack definition from databaase ...");

            g2D.setPaint(filling);
            g2D.fill(shapeObject.getShape());
            // draw a boundary of the shape
            g2D.setPaint(Color.BLACK);
            g2D.draw(shapeObject.getShape());

            g2D.setTransform(old);
        }



    }

}
