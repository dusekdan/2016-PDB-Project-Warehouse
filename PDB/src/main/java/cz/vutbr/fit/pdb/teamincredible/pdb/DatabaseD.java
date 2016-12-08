package cz.vutbr.fit.pdb.teamincredible.pdb;

import com.sun.deploy.security.WIExplorerSigningCertStore;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.tsql2lib.TSQL2Adapter;
import cz.vutbr.fit.tsql2lib.TSQL2Types;
import cz.vutbr.fit.tsql2lib.TypeMapper;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dan on 12/2/2016.
 */
public class DatabaseD {
    private static String userName;
    private static String password;

    public static void setUserName(String value)
    {
        userName = value;
    }

    public static void setPassword(String value)
    {
        password = value;
    }

    private static OracleDataSource dataSource;

    public static void init()
    {
        try {
            dataSource = new OracleDataSource();
            dataSource.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            dataSource.setUser(userName);
            dataSource.setPassword(password);

            System.out.print("DB initialized successfully.");
        }
        catch (SQLException e)
        {
            System.out.print("Database initialization failed. Error code: " + e.getErrorCode());
            throw new ExceptionInInitializerError("Initial Database Connection not established. Sorry.");
        }
    }

    public static Connection getConnection()
    {
        Connection connection = null;

        try
        {
            connection = dataSource.getConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return connection;
    }

    public static void closeConnection()
    {
        try{
            dataSource.close();
            System.out.println("Closing connection to datasource.");
        }
        catch (Exception e)
        {
            System.out.println("Unable to close connection to datasource. Whatever, it's deprecated anyway.");
        }

    }

    public static boolean testConnection()
    {
        if (userName != null &&  password != null)
        {
            if (DatabaseD.getConnection() != null)
                return true;
            else
                return false;
        }
        else
        {
            System.out.println("User name or password to database must not be null.");
            return false;
        }
    }


    /*
     * Good record insertion, update and modification section
     */

    public static boolean InsertGood(Good good)
    {
        // One connection for the whole insertion logic
        Connection conn = getConnection();

        boolean isInsertedBase = false;
        int affectedRowId; // Serves as identification of the record to be further updated with SI_* functions


        // Insert basic values
        try {
            // Configure connection not to autocommit
            conn.setAutoCommit(false);

            affectedRowId = InsertGoodBase(good, conn);
            if (affectedRowId != -1)
                isInsertedBase = true;
        }
        catch (Exception e)
        {
            System.out.println("Unable to save basic part to database. Message: " + e.getMessage());
            return false;
        }

        InsertGoodMediaPart(conn, affectedRowId, good);

        try {
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception e)
        {
            System.out.println("Failed to insert Good record to the database. Message: " + e.getMessage());
            return false;
        }

        return isInsertedBase;
    }

    private static boolean InsertGoodMediaPart(Connection conn, int affectedRowId, Good good)
    {
        OrdImage imgProxy = null;

        // Insert the media data
        try(OraclePreparedStatement selectStatement = (OraclePreparedStatement) conn.prepareStatement(
                "SELECT GOODS_PHOTO FROM GOODS WHERE GOODS_ID = ? FOR UPDATE")){

            // Pass ID of newly inserted row to SELECT statement parameter
            selectStatement.setInt(1, affectedRowId);

            // Prepare file content to be saved to database
            imgProxy = LoadImageFromFile(conn, good.getImgFilePath(), selectStatement);

            if (!SaveImageToDatabase(conn, imgProxy, affectedRowId))
                return false;

            if (!CreateImageFeatures(conn, affectedRowId))
                return false;
        }
        catch (Exception e)
        {
            System.out.println("Unable to save media part to database. Message: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean CreateImageFeatures(Connection conn, int affectedRowId)
    {
        // Prepare basic record for features
        try(OraclePreparedStatement featuresStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS G SET G.GOODS_PHOTO_SI = SI_StillImage(G.GOODS_PHOTO.getContent()) WHERE GOODS_ID = ?")) {
            featuresStatement.setInt(1, affectedRowId);
            featuresStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println("Creation of basic feature image failed. Message: " + e.getMessage());
            return false;
        }

        // Create features and store them in database
        try(OraclePreparedStatement featureExtractionStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS SET "
                        + "GOODS_PHOTO_AC = SI_AverageColor(GOODS_PHOTO_SI), "
                        + "GOODS_PHOTO_CH = SI_ColorHistogram(GOODS_PHOTO_SI), "
                        + "GOODS_PHOTO_PC = SI_PositionalColor(GOODS_PHOTO_SI), "
                        + "GOODS_PHOTO_TX = SI_Texture(GOODS_PHOTO_SI) "
                        + "WHERE GOODS_ID = ?")){
            featureExtractionStatement.setInt(1, affectedRowId);
            featureExtractionStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println("Creation of features failed. Message: " + e.getMessage());
            return false;
        }

        return true;
    }


    private static boolean SaveImageToDatabase(Connection conn, OrdImage imageProxy, int itemId)
    {
        try(OraclePreparedStatement updateStatement = (OraclePreparedStatement) conn.prepareStatement(
                "UPDATE GOODS SET GOODS_PHOTO = ? WHERE GOODS_ID = ?")) {
            updateStatement.setORAData(1, imageProxy);
            updateStatement.setInt(2, itemId);
            updateStatement.executeUpdate();

            return true;
        }
        catch (Exception e)
        {
            System.out.println("Error occurred during saving image to database. Message: " + e.getMessage());
            return false;
        }
    }

    private static OrdImage LoadImageFromFile(Connection conn, String path, OraclePreparedStatement recordStatement) throws SQLException, IOException {
        OrdImage imgProxy = null;

        // Execute statement and get data to imgProxy
        try (OracleResultSet resultSet = (OracleResultSet) recordStatement.executeQuery()) {
            if (resultSet.next())
                imgProxy = (OrdImage) resultSet.getORAData("GOODS_PHOTO", OrdImage.getORADataFactory());
        }

        // Load image from file
        imgProxy.loadDataFromFile(path);
        imgProxy.setProperties();

        return imgProxy;
    }



    private static int InsertGoodBase(Good good, Connection conn) throws SQLException
    {
        String returnCols[] = { "GOODS_ID" };
        PreparedStatement statement = getConnection().prepareStatement(
          "INSERT INTO GOODS (GOODS_VOLUME, GOODS_NAME, GOODS_PHOTO, GOODS_PRICE) VALUES (?, ?, ordsys.ordimage.init(), ?)",
                returnCols
           );

        int insertedRowId = -1;

        try
        {
            statement.setDouble(1, good.getVolume());
            statement.setString(2, good.getName());
            statement.setDouble(3, good.getPrice());

            try
            {
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0)
                {
                    System.out.println("Inserting record failed. No rows affected.");
                }

                insertedRowId = GetInsertedRowID(statement);

            }
            catch (Exception e)
            {
                System.out.println("Updating prepared statement error. Message: " + e.getMessage());
                return -1;
            }
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong when base inserting Good item. Message: " + e.getMessage());
            return -1;
        }
        finally
        {
            statement.close();
        }

        return insertedRowId;
    }

    private static int GetInsertedRowID(PreparedStatement statement)
    {
        int rowId = -1;

        try (ResultSet resultSet = statement.getGeneratedKeys())
        {
            if (resultSet.next())
            {
                rowId = resultSet.getInt(1);
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed to retrieve ID of inserted record. Message: " + e.getMessage());
        }

        return rowId;
    }


    public static List<Good> GetGoods()
    {
        List<Good> entities = new ArrayList<>();

        try (Connection connection = dataSource.getConnection())
        {
            System.out.println("Connection datasource.getconnection tried.");
            try (
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Goods");
                    ResultSet resultSet = statement.executeQuery();
            )
            {
                System.out.println("Second try...");
                while (resultSet.next())
                {
                    System.out.println("Adding entity...");
                    Good entity = new Good();
                    entity.setId(resultSet.getInt(1));  // NOTE: Indexing in oracle database starts with 1 (historically, mathematically, yeah)
                    entity.setName(resultSet.getString(3));
                    //entity.setPhoto(resultSet.getString(4));
                    //entity.setCount(); // TODO: this could be a problem
                    // TODO: Set column types accordingly, so this checks out with reality
                    entities.add(entity);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception when selecting entities occured. " + e.getMessage());
            return entities;
        }

        return entities;
    }

    /**
     *
     *
     */
    public static void initDBStruct() {
        //  https://github.com/rychly/tsql2lib/blob/master/tsql2sample/src/main/java/cz/vutbr/fit/tsql2sample/App.java
        Statement stmt = null;
        try {
            stmt =  DatabaseD.getConnection().createStatement();
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

            stmt.execute("CREATE TABLE racks (\n" +
                    "racks_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL," +
                    "racks_type NUMBER(10)," +
                    "racks_geometry SDO_GEOMETRY, " +
                    "racks_rotation NUMBER(1), " +
                    "CONSTRAINT racks_pk PRIMARY KEY (racks_id)" +
                    ")"
            );

            stmt.execute("CREATE TABLE rack_definitions ( " +
                    "rack_defs_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL," +
                    "rack_defs_name VARCHAR(32)," +
                    "rack_defs_capacity NUMBER(32,2)," +
                    "rack_defs_size_x NUMBER(10),"+
                    "rack_defs_size_y NUMBER(10),"+
                    "rack_defs_shape SDO_GEOMETRY,"+
                    "CONSTRAINT rack_definitions_pk PRIMARY KEY (rack_defs_id)" +
                    ")"
            );

            stmt.execute("CREATE TABLE goods ( " +
                    "goods_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL," +
                    "goods_volume NUMBER(32,2)," +
                    "goods_name VARCHAR(32)," +
                    "goods_photo ORDSYS.ORDImage,"+
                    "goods_photo_si ORDSYS.SI_StillImage," +
                    "goods_photo_ac ORDSYS.SI_AverageColor," +
                    "goods_photo_ch ORDSYS.SI_ColorHistogram," +
                    "goods_photo_pc ORDSYS.SI_PositionalColor," +
                    "goods_photo_tx ORDSYS.SI_Texture," +
                    "goods_price NUMBER(32,2)," +
                    "CONSTRAINT goods_pk PRIMARY KEY (goods_id)"+
                    ")"
            );



            stmt.execute("CREATE TABLE rack_goods (" +
                    "racks_id NUMBER(10) NOT NULL," +
                    "goods_id NUMBER(10) NOT NULL," +
                    "rack_goods_count NUMBER(32) NOT NULL," +
                    "valid_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"+
                    "valid_to TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"+
                    "CONSTRAINT rack_goods_pk PRIMARY KEY (racks_id, goods_id)" +
                    " )"
            );
          
          stmt.execute("ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_rack" +
                         "  FOREIGN KEY (racks_id)" +
                         "  REFERENCES racks(racks_id)");
            System.out.println("alter1");
            
            stmt.execute("ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_goods" +
                         "  FOREIGN KEY (goods_id)" +
                         "  REFERENCES goods(goods_id)");
            System.out.println("alter2");
            
            stmt.execute("ALTER TABLE racks ADD CONSTRAINT fk_racks_type_rack_defs" +
                         "  FOREIGN KEY (racks_type)" +
                         "  REFERENCES rack_definitions(rack_defs_id)");
            System.out.println("alter3");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    

    public DatabaseD(){}
}
