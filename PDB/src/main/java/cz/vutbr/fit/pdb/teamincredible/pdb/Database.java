package cz.vutbr.fit.pdb.teamincredible.pdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sucha
 */
public class Database {

    private String username;
    private String passwd;

    private boolean connected;
    
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    
    
    
    public Database() throws SQLException {

    }

    
    public boolean executeSQLFile(String filename) {
        //TODO: not implemented
        return false;
    }
    
    public ResultSet executeQuery(String SQLQuery) throws SQLException {
            // create a OracleDataSource instance
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            /**
             * *
             * To set System properties, run the Java VM with the following at
             * its command line: ... -Dlogin=LOGIN_TO_ORACLE_DB
             * -Dpassword=PASSWORD_TO_ORACLE_DB ... or set the project
             * properties (in NetBeans: File / Project Properties / Run / VM
             * Options)
             */
            ods.setUser(username);
            ods.setPassword(passwd);
            /**
             *
             */
            // connect to the database

            try (Connection conn = ods.getConnection()) {
                // create a Statement
                try (Statement stmt = conn.createStatement()) {
                    // select something from the system's dual table
                    try (ResultSet rset = stmt.executeQuery(
                            SQLQuery)) {
                        // iterate through the result and print the values
                        return rset;
                    } // close the ResultSet
                } // close the Statement
            } // close the connection

    }

    public boolean testConnection() throws Exception {
        System.err.println("*** Connecting to the Oracle db. and running a simple query ***");
        try {
            // create a OracleDataSource instance
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            /**
             * *
             * To set System properties, run the Java VM with the following at
             * its command line: ... -Dlogin=LOGIN_TO_ORACLE_DB
             * -Dpassword=PASSWORD_TO_ORACLE_DB ... or set the project
             * properties (in NetBeans: File / Project Properties / Run / VM
             * Options)
             */
            ods.setUser(username);
            ods.setPassword(passwd);
            /**
             *
             */
            // connect to the database
            
            try (Connection conn = ods.getConnection()) {
                // create a Statement
                try (Statement stmt = conn.createStatement()) {
                    // select something from the system's dual table
                    try (ResultSet rset = stmt.executeQuery(
                            "select 1+2 as col1, 3-4 as col2 from dual")) {
                        // iterate through the result and print the values
                        while (rset.next()) {
                            System.err.println("col1: '" + rset.getString(1)
                                    + "'\tcol2: '" + rset.getString(2) + "'");
                        }
                    } // close the ResultSet
                } // close the Statement
            } // close the connection
        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
            return false;
        }

        return true;
        
    }

}
