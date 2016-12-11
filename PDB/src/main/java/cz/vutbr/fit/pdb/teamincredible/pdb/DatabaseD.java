package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.controller.GoodsController;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;

/**
 * Created by Dan on 12/2/2016 Contains logic for connection to database and
 * working with database object
 *
 * Note: Possible enhancement would be to implement some sort of ORM-style class
 * of our own, but that appears to be an overkill for the project of given size
 * and purpose.
 */
public class DatabaseD {

    // Database connection credentials
    private static String userName;
    private static String password;

    // Property holding Oracle Data Source used application wide
    private static OracleDataSource dataSource;

    /**
     * Setter for String userName property
     */
    public static void setUserName(String value) {
        userName = value;
    }

    /**
     * Setter for String password property
     */
    public static void setPassword(String value) {
        password = value;
    }

    /**
     * Initializes connection to database
     */
    public static void init() {
        try {
            dataSource = new OracleDataSource();
            dataSource.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            dataSource.setUser(userName);
            dataSource.setPassword(password);

            System.out.println("D: Database initialized successfully.");
        } catch (SQLException e) {
            System.out.print("Database initialization failed. Error code: " + e.getErrorCode());
            throw new ExceptionInInitializerError("Initial Database Connection not established. Sorry.");
        }
    }

    /**
     * Returns new instance of connection
     *
     * @return Connection active connection to Oracle Data Source or null on
     * connection failure
     */
    public static Connection getConnection() {
        Connection connection = null;

        // Remarks: This is here because of the heavy load on school servers, getConnection function tends to end up
        // without seizing the connection. This way, it tries until it gets it. While this is really not fair, it
        // certainly is efficient way around.
        while (true) {
            try {
                connection = dataSource.getConnection();
                Thread.sleep(100);
                break;
            } catch (SQLException e) {
                System.out.println("Exception during retrieving connection to Oracle Data Source. Message: " + e.getMessage() + ". Proceeding to make another attempt on seizing the connection.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    /**
     * Closes connection to Oracle Data Source
     *
     * @deprecated Method uses OracleDataSource.close() method that is also
     * deprecated
     * @implNote Method is called in JavaFX application stop() method to
     * terminate possibly opened connection before exiting
     */
    public static void closeConnection() {
        try {
            dataSource.close();
            System.out.println("Closing connection to datasource.");
        } catch (Exception e) {
            System.out.println("Unable to close connection to datasource. Whatever, it's deprecated anyway.");
        }
    }

    /**
     * Tests whether connection to database could be established or not
     *
     * @return boolean True for established connection, otherwise false
     */
    public static boolean testConnection() {
        if (userName != null && password != null) {
            return DatabaseD.getConnection() != null;
        } else {
            System.out.println("User name or password to database must not be null.");
            return false;
        }
    }

    /**
     * Helper method returning inserted row ID (usefull for multipart queries
     * and consequential updates)
     *
     * @param statement PreparedStatement Insert query which inserted row is to
     * be returned
     * @return
     */
    public static int GetInsertedRowID(PreparedStatement statement)
    {
        int rowId = -1;

        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if (resultSet.next()) {
                rowId = resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve ID of inserted record. Message: " + e.getMessage());
        }

        return rowId;
    }

    /*
     * Good record insertion, update and modification section
     */

    /**
     * Inserts Good object into database, both media and text part of it
     *
     * @param good Good object to be inserted into database
     * @return Boolean true on success, false otherwise
     */
    public static boolean InsertGood(Good good) {
        // One connection for the whole insertion logic
        Connection conn = getConnection();

        boolean isInsertedBase = false;
        int affectedRowId; // Serves as identification of the record to be further updated with SI_* functions

        // Insert basic values
        try {
            // Configure connection not to autocommit
            conn.setAutoCommit(false);

            affectedRowId = InsertGoodBase(good);
            if (affectedRowId != -1) {
                isInsertedBase = true;
            }
        } catch (Exception e) {
            System.out.println("Unable to save basic part to database. Message: " + e.getMessage());
            return false;
        }

        InsertGoodMediaPart(conn, affectedRowId, good);

        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            System.out.println("Failed to insert Good record to the database. Message: " + e.getMessage());
            return false;
        }

        return isInsertedBase;
    }

    /**
     * Method responsible for insertion and extraction of features for media part of the insert query
     * @param conn Connection to oracle data source
     * @param affectedRowId int identification of the row to which insertion of
     * media part corresponds
     * @param good Good object which media properties should be inserted
     * @return Boolean true on success, false otherwise
     */
    private static boolean InsertGoodMediaPart(Connection conn, int affectedRowId, Good good) {
        OrdImage imgProxy = null;

        // Insert the media data
        try (OraclePreparedStatement selectStatement = (OraclePreparedStatement) conn.prepareStatement(
                "SELECT GOODS_PHOTO FROM GOODS WHERE GOODS_ID = ? FOR UPDATE")) {

            // Pass ID of newly inserted row to SELECT statement parameter
            selectStatement.setInt(1, affectedRowId);

            // Prepare file content to be saved to database
            imgProxy = LoadImageFromFile(good.getImgFilePath(), selectStatement);

            if (!SaveImageToDatabase(conn, imgProxy, affectedRowId)) {
                return false;
            }

            if (!CreateImageFeatures(conn, affectedRowId)) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Unable to save media part to database. Message: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Creates StillImage features for the OrdImage object
     *
     * @param conn Connection to oracle data source
     * @param affectedRowId int identification of the row to which insertion of
     * media part corresponds
     * @return Boolean true on success, false otherwise
     */
    private static boolean CreateImageFeatures(Connection conn, int affectedRowId) {
        // Prepare basic record for features
        try (OraclePreparedStatement featuresStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS G SET G.GOODS_PHOTO_SI = SI_StillImage(G.GOODS_PHOTO.getContent()) WHERE GOODS_ID = ?")) {
            featuresStatement.setInt(1, affectedRowId);
            featuresStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Creation of basic feature image failed. Message: " + e.getMessage());
            return false;
        }

        // Create features and store them in database
        try (OraclePreparedStatement featureExtractionStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS SET "
                + "GOODS_PHOTO_AC = SI_AverageColor(GOODS_PHOTO_SI), "
                + "GOODS_PHOTO_CH = SI_ColorHistogram(GOODS_PHOTO_SI), "
                + "GOODS_PHOTO_PC = SI_PositionalColor(GOODS_PHOTO_SI), "
                + "GOODS_PHOTO_TX = SI_Texture(GOODS_PHOTO_SI) "
                + "WHERE GOODS_ID = ?")) {
            featureExtractionStatement.setInt(1, affectedRowId);
            featureExtractionStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Creation of features failed. Message: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Saves OrdImage file to database
     *
     * @param conn Connection to oracle data source
     * @param imageProxy OrdImage representing image to be saved to database
     * @param itemId int identification of the corresponding record
     * @return Boolean true on success, false otherwise
     */
    private static boolean SaveImageToDatabase(Connection conn, OrdImage imageProxy, int itemId) {
        try (OraclePreparedStatement updateStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS SET GOODS_PHOTO = ? WHERE GOODS_ID = ?")) {
            updateStatement.setORAData(1, imageProxy);
            updateStatement.setInt(2, itemId);
            updateStatement.executeUpdate();

            return true;
        } catch (Exception e) {
            System.out.println("Error occurred during saving image to database. Message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads image from file to OrdImage format
     *
     * @param path String path to file to be loaded
     * @param recordStatement OraclePreparedStatement relevant to current
     * insertion
     * @return OrdImage loaded image from file
     * @throws SQLException on possible faults.
     * @throws IOException on possible faults
     */
    private static OrdImage LoadImageFromFile(String path, OraclePreparedStatement recordStatement) throws SQLException, IOException {
        OrdImage imgProxy = null;

        // Execute statement and get data to imgProxy
        try (OracleResultSet resultSet = (OracleResultSet) recordStatement.executeQuery()) {
            if (resultSet.next()) {
                imgProxy = (OrdImage) resultSet.getORAData("GOODS_PHOTO", OrdImage.getORADataFactory());
            }
        }

        // Load image from file
        imgProxy.loadDataFromFile(path);
        imgProxy.setProperties();

        return imgProxy;
    }

    /**
     * Inserts textual part of the GOODS table record
     *
     * @param good Good object to be inserted
     * @return int number identification of the inserted row
     * @throws SQLException on possible faults
     */
    private static int InsertGoodBase(Good good) throws SQLException {
        String returnCols[] = {"GOODS_ID"};

        int insertedRowId = -1;

        try (PreparedStatement statement = getConnection().prepareStatement(
                "INSERT INTO GOODS (GOODS_VOLUME, GOODS_NAME, GOODS_PHOTO, GOODS_PRICE) VALUES (?, ?, ordsys.ordimage.init(), ?)",
                returnCols
        )) {
            statement.setDouble(1, good.getVolume());
            statement.setString(2, good.getName());
            statement.setDouble(3, good.getPrice());

            try {
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    System.out.println("Inserting record failed. No rows affected.");
                }

                insertedRowId = GetInsertedRowID(statement);

            } catch (Exception e) {
                System.out.println("Updating prepared statement error. Message: " + e.getMessage());
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Something went wrong when base inserting Good item. Message: " + e.getMessage());
            return -1;
        }

        return insertedRowId;
    }

    /**
     * Retrieves entities from GOODS table
     *
     * @return List<Good> List of Good object representing entities stored in
     * GOODS table. No SI_* types are retrieved.
     */
    public static List<Good> GetGoods() {
        List<Good> entities = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            try (
                    OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement("SELECT * FROM Goods");
                    OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();) {
                // Retrieving entities from database
                while (resultSet.next()) {
                    System.out.println("Retrieving entity from GOODS table...");

                    // Retrieve raw data and cast it to OrdImage
                    OrdImage entityPhoto = (OrdImage) resultSet.getORAData(4, OrdImage.getORADataFactory());

                    Good entity = new Good();
                    entity.setId(resultSet.getInt(1));          // NOTE: Indexing in oracle database starts with 1 (historically, mathematically, etc.)
                    entity.setVolume(resultSet.getDouble(2));
                    entity.setName(resultSet.getString(3));
                    entity.setPhoto(entityPhoto);
                    entity.setPrice(resultSet.getDouble(10));

                    entities.add(entity);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in selecting GOODS entities occurred. Message: " + e.getMessage());
            return entities;
        }

        return entities;
    }


    /**
     * Retrieve images from the database that are similar to the currently selected one
     * @param referenceId Int numeric identification of the record to which we select similar items
     * @return List of Good objects with similarity property filled
     */
    public static List<Good> GetSimilarGoods(int referenceId)
    {
        List<Good> entities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection())
        {
            try(
                    OraclePreparedStatement statement = (OraclePreparedStatement) conn.prepareStatement(
                            "SELECT sourceImage.GOODS_ID AS source, targetImages.GOODS_ID AS target, " +
                                    "SI_ScoreByFtrList( new SI_FeatureList( sourceImage.GOODS_PHOTO_AC, 0.3, sourceImage.GOODS_PHOTO_CH, 0.3, " +
                                    "sourceImage.GOODS_PHOTO_PC, 0.1, sourceImage.GOODS_PHOTO_TX, 0.3 ), targetImages.GOODS_PHOTO_SI) as similarity, targetImages.GOODS_ID, targetImages.GOODS_VOLUME, targetImages.GOODS_NAME, " +
                                    " targetImages.GOODS_PHOTO, targetImages.GOODS_PRICE FROM GOODS sourceImage, GOODS targetImages " +
                                    " WHERE sourceImage.GOODS_ID <> targetImages.GOODS_ID AND sourceImage.GOODS_ID = ? " +
                                    " ORDER BY similarity ASC ")
            )
            {
                statement.setInt(1, referenceId);
                OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();

                // Retrieving entities from database
                while (resultSet.next()) {
                    System.out.println("Retrieving similar entity from GOODS table...");

                    // Retrieve raw data and cast it to OrdImage
                    OrdImage entityPhoto = (OrdImage) resultSet.getORAData(7, OrdImage.getORADataFactory());

                    Good entity = new Good();
                    entity.setId(resultSet.getInt(4));          // NOTE: Indexing in oracle database starts with 1 (historically, mathematically, etc.)
                    entity.setVolume(resultSet.getDouble(5));
                    entity.setName(resultSet.getString(6));
                    entity.setPhoto(entityPhoto);
                    entity.setPrice(resultSet.getDouble(8));
                    entity.setSimilarity(resultSet.getDouble("similarity"));

                    entities.add(entity);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("E: Exception in selecting similar GOODS entities. Message: " + e.getMessage());
        }

        return entities;
    }

    /**
     * Returns good record identified by id
     *
     * @param id Int identification of the record to be retrieved
     * @return Corresponding Good object on success, null on false
     */
    public static Good GetGoodById(int id) {
        try (Connection connection = getConnection()) {
            try (OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement("SELECT * FROM GOODS WHERE GOODS_ID = ?")) {
                statement.setInt(1, id);
                OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();

                if (resultSet.next()) {
                    OrdImage itemPhoto = (OrdImage) resultSet.getORAData(4, OrdImage.getORADataFactory());

                    Good item = new Good();
                    item.setId(resultSet.getInt(1));
                    item.setVolume(resultSet.getDouble(2));
                    item.setName(resultSet.getString(3));
                    item.setPhoto(itemPhoto);
                    item.setPrice(resultSet.getDouble(10));


                    item.setRealImageData(PrepareImageFromBlob(itemPhoto.getBlobContent()));


                    return item;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in selecting specific GOODS item occurred. Message: " + e.getMessage());
            return null;
        }
    }


    /**
     * Remove record from GOODS table
     * @param goodId Int identification of GOODS table record to be removed
     * @return boolean true on success of operation, false otherwise
     */
    public static boolean RemoveGood(int goodId)
    {
        try (Connection connection = getConnection())
        {
            try (OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement("DELETE FROM GOODS WHERE GOODS_ID = ?"))
            {
                statement.setInt(1, goodId);

                return statement.executeUpdate() != 0;
            }
        }
        catch (Exception e)
        {
            System.out.println("E: Unable to remove GOOD record from databse");
            return false;
        }
    }


    /**
     * From BLOB retrieved as Image data from database creates BufferedImage which then translates to Image (javafx package)
     * @return Image converted image
     */
    private static Image PrepareImageFromBlob(Blob content)
    {
        BufferedImage image = null;
        try (InputStream in = content.getBinaryStream()) {
            image = ImageIO.read(in);
        } catch (Exception e) {
            System.out.println("E: Unable to retrieve blob content of a image file to display. Message: " + e.getMessage());
        }

        if (image != null)
        {
            return SwingFXUtils.toFXImage(image, null);
        }

        return null;
    }


    /*
     * Database initialization part
     */

    /**
     *
     *
     */
    @SuppressWarnings("SqlDialectInspection")
    public static void initDBStruct() {
        //  https://github.com/rychly/tsql2lib/blob/master/tsql2sample/src/main/java/cz/vutbr/fit/tsql2sample/App.java
        Statement stmt = null;
        try {
            stmt = DatabaseD.getConnection().createStatement();
            try {
                stmt.execute("DROP TABLE racks CASCADE CONSTRAINTS");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            try {
                stmt.execute("DROP TABLE rack_definitions CASCADE CONSTRAINTS");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            try {
                stmt.execute("DROP TABLE goods CASCADE CONSTRAINTS");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            try {
                stmt.execute("DROP TABLE rack_goods CASCADE CONSTRAINTS");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }

            stmt.execute("CREATE TABLE racks (\n"
                    + "racks_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL,"
                    + "racks_type NUMBER(10),"
                    + "racks_geometry SDO_GEOMETRY, "
                    + "racks_rotation NUMBER(1), "
                    + "CONSTRAINT racks_pk PRIMARY KEY (racks_id)"
                    + ")"
            );

            stmt.execute("CREATE TABLE rack_definitions ( "
                    + "rack_defs_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL,"
                    + "rack_defs_name VARCHAR(32),"
                    + "rack_defs_capacity NUMBER(32,2),"
                    + "rack_defs_size_x NUMBER(10),"
                    + "rack_defs_size_y NUMBER(10),"
                    + "rack_defs_shape SDO_GEOMETRY,"
                    + "CONSTRAINT rack_definitions_pk PRIMARY KEY (rack_defs_id)"
                    + ")"
            );

            stmt.execute("CREATE TABLE goods ( "
                    + "goods_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL,"
                    + "goods_volume NUMBER(32,2),"
                    + "goods_name VARCHAR(32),"
                    + "goods_photo ORDSYS.ORDImage,"
                    + "goods_photo_si ORDSYS.SI_StillImage,"
                    + "goods_photo_ac ORDSYS.SI_AverageColor,"
                    + "goods_photo_ch ORDSYS.SI_ColorHistogram,"
                    + "goods_photo_pc ORDSYS.SI_PositionalColor,"
                    + "goods_photo_tx ORDSYS.SI_Texture,"
                    + "goods_price NUMBER(32,2),"
                    + "CONSTRAINT goods_pk PRIMARY KEY (goods_id)"
                    + ")"
            );

            stmt.execute("CREATE TABLE rack_goods ("
                    + "racks_id NUMBER(10) NOT NULL,"
                    + "goods_id NUMBER(10) NOT NULL,"
                    + "rack_goods_count NUMBER(32) NOT NULL,"
                    + "valid_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                    + "valid_to TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                    + "CONSTRAINT rack_goods_pk PRIMARY KEY (racks_id, goods_id, valid_from, valid_to)"
                    + " )"
            );

            stmt.execute("ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_rack"
                    + "  FOREIGN KEY (racks_id)"
                    + "  REFERENCES racks(racks_id)");
            System.out.println("alter1");
            
            stmt.execute("ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_goods"
                    + "  FOREIGN KEY (goods_id)"
                    + "  REFERENCES goods(goods_id)");
            System.out.println("alter2");
            
            stmt.execute("ALTER TABLE racks ADD CONSTRAINT fk_racks_type_rack_defs"
                    + "  FOREIGN KEY (racks_type)"
                    + "  REFERENCES rack_definitions(rack_defs_id)");
            System.out.println("alter3");

            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadInitData() {
        Statement stmt = null;

        try {
            stmt = DatabaseD.getConnection().createStatement();

            stmt.execute("insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)"
                    + ""
                    + "values (1, 'L', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  20,0,  20,10,   10,10,   10,30,   0,30,  0,0)\n"
                    + "	))");

            stmt.execute(" insert into racks (racks_type, racks_geometry, racks_rotation)\n"
                    + "  values (1, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(30,30,  50,30,  50,40,   40,40,   40,60,   30,60,  30,30)\n"
                    + "	), 3)");

            stmt.execute("insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values (2, 'T', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,10,   20,10,   20,20,   10,20,  10,30,   0,30,  0,0)\n"
                    + "	))");

            stmt.execute(" insert into racks (racks_type, racks_geometry, racks_rotation)\n"
                    + "  values (2, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(100,20,  110,20,  110,30,   120,30,   120,40,   110,40,  110,50,   100,50,  100,20)\n"
                    + "	), 0)");

            stmt.execute("insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values (3, 'II', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,30,   0,30,   0,0)\n"
                    + "	))");

            stmt.execute("   insert into racks (racks_type, racks_geometry, racks_rotation)\n"
                    + "  values (3, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(50,90,  60,90,  60,120,   50,120,   50,90)\n"
                    + "	), 0)");

            stmt.execute("insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values (4, 'III', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,40,   0,40,   0,0)\n"
                    + "	))");

            stmt.execute("insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values ('I', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,20,   0,20,   0,0)\n"
                    + "	))");

            stmt.execute("insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values ('O', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 4), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(10,0,  20,10,  10,20)\n"
                    + "	))");

            stmt.execute(" insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values ('OO', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 4), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(15,0,  30,15,  15,30)\n"
                    + "	))");

            stmt.execute("insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)\n"
                    + "values ('D', 1000, 10, 30, \n"
                    + "SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon\n"
                    + "		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)\n"
                    + "		SDO_ORDINATE_ARRAY(0,0,  20,0,  20,20, 0,20,   0,0)\n"
                    + "	))");

            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static boolean InsertGoodIntoStorage(int goodID, int stockID, int count) {
        Statement stmt = null;
        
        try {
            stmt = DatabaseD.getConnection().createStatement();
            
            ResultSet executeQuery = stmt.executeQuery("SELECT rack_goods_count FROM rack_goods WHERE"
                    + " VALID_TO > CURRENT_TIMESTAMP"
                    + " AND goods_id = " + goodID + " AND racks_ID = " + stockID);
            int newCount = 0;
            
            if (executeQuery.next()) {
                newCount = executeQuery.getInt(1) + count;

                executeQuery = stmt.executeQuery("SELECT COUNT(rack_goods_count) FROM rack_goods WHERE"
                        + " VALID_TO > CURRENT_TIMESTAMP"
                        + " AND goods_id = " + goodID + " AND racks_ID = " + stockID);

                int numResults = 0;
                if (executeQuery.next()) {
                    numResults = executeQuery.getInt(1);
                }
                if (numResults == 1) {
                    stmt.execute("UPDATE rack_goods SET valid_to = CURRENT_TIMESTAMP"
                            + " WHERE goods_id = " + goodID + ""
                            + " AND racks_ID = " + stockID
                            + "AND valid_to = TO_TIMESTAMP('9999-12-31-23.59.59.999999','YYYY-MM-DD-HH24.MI.SS.FF')"
                            + "");
                } else {
                    System.err.println("DOOMED" + numResults);
                }
                
                stmt.execute("INSERT INTO rack_goods VALUES("
                        + stockID + ","
                        + goodID + ","
                        + newCount + ","
                        + "CURRENT_TIMESTAMP,"
                        + "TO_TIMESTAMP('9999-12-31-23.59.59.999999','YYYY-MM-DD-HH24.MI.SS.FF')"
                        + ")");

            } else {

                stmt.execute("INSERT INTO rack_goods VALUES("
                        + stockID + ","
                        + goodID + ","
                        + count + ","
                        + "CURRENT_TIMESTAMP,"
                        + "TO_TIMESTAMP('9999-12-31-23.59.59.999999','YYYY-MM-DD-HH24.MI.SS.FF')"
                        + ")");
            }

        } catch (SQLException ex) {
              System.err.println("Insert to "+stockID+" failed for good "+goodID+" and count "+count);
            Logger.getLogger(DatabaseD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public static boolean MoveGoodFromTo(int fromGID, int fromSID, int toGID, int toSID, int count) {
        boolean removed = RemoveGoodFromStorage(fromGID, fromSID, count);
        boolean inserted = InsertGoodIntoStorage(toGID, toSID, count);
        return removed && inserted;
    }
    
    public static boolean RemoveGoodFromStorage(int goodID, int stockID, int count) {
        Statement stmt = null;
        
        try {
            
            stmt = DatabaseD.getConnection().createStatement();

            ResultSet executeQuery = stmt.executeQuery("SELECT rack_goods_count FROM rack_goods WHERE"
                    + " VALID_TO > CURRENT_TIMESTAMP"
                    + " AND goods_id = " + goodID + " AND racks_ID = " + stockID);
            int newCount = 0;
            if (executeQuery.next()) {
                newCount = executeQuery.getInt(1) - count;
            }
            executeQuery = stmt.executeQuery("SELECT COUNT(rack_goods_count) FROM rack_goods WHERE"
                    + " VALID_TO > CURRENT_TIMESTAMP"
                    + " AND goods_id = " + goodID + " AND racks_ID = " + stockID);
            
            int numResults = 0;
            if (executeQuery.next()) {
                numResults = executeQuery.getInt(1);
            }
            if (numResults == 1) {
                stmt.execute("UPDATE rack_goods SET valid_to = CURRENT_TIMESTAMP"
                        + " WHERE goods_id = " + goodID + ""
                        + " AND racks_ID = " + stockID
                        + "AND valid_to = TO_TIMESTAMP('9999-12-31-23.59.59.999999','YYYY-MM-DD-HH24.MI.SS.FF')"
                        + "");
            }
            
            DatabaseD.InsertGoodIntoStorage(goodID, stockID, newCount);

        } catch (SQLException ex) {
            System.err.println("Remove from "+stockID+" failed for good "+goodID+" and count "+count);
            Logger.getLogger(DatabaseD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    /**
     * Get all records from racks_goods table
     *
     * @return
     */
    public static ObservableList<StoreActivityRecord> GetStoreHistory() {
        
        ObservableList<StoreActivityRecord> data = FXCollections.observableArrayList();

        try (Connection connection = dataSource.getConnection()) {
            try (
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM rack_goods");
                    ResultSet resultSet = statement.executeQuery();) {
                while (resultSet.next()) {
                    StoreActivityRecord record = new StoreActivityRecord(
                            resultSet.getInt(2),
                            resultSet.getInt(1),
                            resultSet.getInt(3),
                            resultSet.getTimestamp(4),
                            resultSet.getTimestamp(5)
                    );
                    data.add(record);
                }

            }
        } catch (Exception e) {
            return data;
        }

        return data;
    }

    /**
     * Method to get goods in racks Returns all records from 'date' to 'date +
     * 24H '
     *
     *
     * @param date -- specified date
     * @return collection of found records
     */
    public static ObservableList<StoreActivityRecord> GetStoreHistory(Calendar date) {
        ObservableList<StoreActivityRecord> data = FXCollections.observableArrayList();

        try (Connection connection = dataSource.getConnection()) {
            Timestamp today = new Timestamp(date.getTimeInMillis());
            Calendar clone = (Calendar) date.clone();
            clone.add(Calendar.DATE, 1);
            Timestamp tomorrow = new Timestamp(clone.getTimeInMillis());
            try (
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT * FROM rack_goods WHERE"
                            + " valid_from > (?) "
                            + "AND valid_from < (?)"
                    );) {
                        statement.setTimestamp(1, today);
                        statement.setTimestamp(2, tomorrow);
                        ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            StoreActivityRecord record = new StoreActivityRecord(
                                    resultSet.getInt(2),
                                    resultSet.getInt(1),
                                    resultSet.getInt(3),
                                    resultSet.getTimestamp(4),
                                    resultSet.getTimestamp(5)
                            );
                            data.add(record);
                        }

                    }
        } catch (Exception e) {
            return data;
        }

        return data;
    }

    public static ObservableList<GoodInRack> getGoodsInRack(int rackID) {
        ObservableList<GoodInRack> ret = FXCollections.observableArrayList();
        Statement stmt;
        try{
              stmt = DatabaseD.getConnection().createStatement();
            ResultSet executeQuery = stmt.executeQuery("SELECT rack_goods.racks_id, rack_goods.rack_goods_count, goods.goods_id, goods.goods_name FROM rack_goods INNER JOIN goods \n" +
                    "ON rack_goods.goods_id=goods.goods_id\n" +
                    "WHERE rack_goods.valid_to > CURRENT_TIMESTAMP AND rack_goods.racks_id = "+ rackID);
            
            while (executeQuery.next()) {
                ret.add(new GoodInRack(
                        executeQuery.getInt(2),
                        executeQuery.getInt(3),
                        executeQuery.getInt(1),
                        executeQuery.getString(4)
                ));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }


    public DatabaseD() {
    }
}
