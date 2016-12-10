package cz.vutbr.fit.pdb.teamincredible.pdb;

import java.awt.Shape;

import javax.swing.*;

import oracle.spatial.geometry.JGeometry;


/**
 * Created by Anna on 03/12/2016.
 */
public class SpatialConverters extends JPanel {


    /**
     * Exception when converting a JGeometry object to a Shape object.
     */
    public static class JGeometry2ShapeException extends Exception {
    };

    /**
     * Make a Shape object from a JGeometry object (the Java representation of
     * SDO_GEOMETRY data). The Shape objects are drawable into Java GUI.
     *
     * @param jGeometry the JGeometry object as an input
     * @return the Shape object corresponding to the input JGeometry object
     * @throws JGeometry2ShapeException if cannot make the Shape object from the
     * JGeometry object
     */
    public static Shape jGeometry2Shape(JGeometry jGeometry) throws JGeometry2ShapeException {
        Shape shape;
        // check a type of JGeometry object
        switch (jGeometry.getType()) {
            // it is a polygon
            case JGeometry.GTYPE_POLYGON:
                
                shape = jGeometry.createShape();
                break;
            // it is something else (we do not know how to convert)
            default:
                throw new JGeometry2ShapeException();
        }
        return shape;
    }

}
