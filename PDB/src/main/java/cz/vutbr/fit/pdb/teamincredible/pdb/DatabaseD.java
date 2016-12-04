package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.Goods;
import oracle.jdbc.pool.OracleDataSource;

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


    /*public static ResultSet ExecuteSQLQuery(String SQLQuery) throws SQLException
    {
        List<Object> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection())
        {
            try (
                    PreparedStatement statement = connection.prepareStatement(SQLQuery);
                    ResultSet resultSet = statement.executeQuery();
            )
            {
                int columnCount = resultSet.getMetaData().getColumnCount();
                while (resultSet.next())
                {

                }

            }
        }
    }*/

    public static List<Goods> GetGoods()
    {
        List<Goods> entities = new ArrayList<>();

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
                    Goods entity = new Goods();
                    entity.setId(resultSet.getInt(0));
                    entity.setTitle(resultSet.getString(2));
                    entity.setPicture(resultSet.getString(3));
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


    public DatabaseD(){}
}
