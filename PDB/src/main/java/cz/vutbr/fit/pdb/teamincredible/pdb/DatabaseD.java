package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.tsql2lib.TSQL2Adapter;
import cz.vutbr.fit.tsql2lib.TSQL2Types;
import cz.vutbr.fit.tsql2lib.TypeMapper;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;

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
        Statement stmtTSQL = null;
        try {
            stmt =  DatabaseD.getConnection().createStatement();
            stmtTSQL = new TSQL2Adapter(DatabaseD.getConnection()).createStatement();
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
                stmtTSQL.execute("DROP TABLE rack_goods CASCADE CONSTRAINTS");
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



            stmtTSQL.execute("CREATE TABLE rack_goods (\n" +
                    "racks_id " +TypeMapper.get(TSQL2Types.INT)+ " NOT NULL,\n" +
                    "goods_id " +TypeMapper.get(TSQL2Types.INT)+ " NOT NULL,\n" +
                    "rack_goods_count " +TypeMapper.get(TSQL2Types.INT)+ ",\n" +
                    "CONSTRAINT rack_goods_pk PRIMARY KEY (racks_id, goods_id)\n" +
                    ") AS TRANSACTION TIME;\n"
            );

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public DatabaseD(){}
}
