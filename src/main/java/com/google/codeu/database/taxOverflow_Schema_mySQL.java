// Import required packages

import java.sql.*;

public class JDBCExample {
  // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  static final String DB_URL = "jdbc:mysql://localhost/STUDENTS";

  //  Database credentials
  static final String USER = "username";
  static final String PASS = "password";

  public static void main(String[] args) {
    Connection conn = null;
    Statement stmt = null;
    try {
      // Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      // Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // Creating a database schema
      Statement sta = con.createStatement();
      int count = sta.executeUpdate("CREATE SCHEMA T_Overflow");
      System.out.println("Schema created.");
      sta.close();

      // Execute a query
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      // need to create array of postID to keep track of answered questions
      String sql =
          "CREATE TABLE PROFILE "
              + "(profileId INTEGER not NULL, "
              + " first VARCHAR, "
              + " last VARCHAR, "
              + " isCPA BIT not NULL, "
              + " PRIMARY KEY ( profileId ))";

      stmt.executeUpdate(sql);
      System.out.println("Created table in given database...");

      // Execute a query
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      // need to create array of responses, per postId
      String sql =
          "CREATE TABLE POST "
              + "(postId INTEGER not NULL UNIQUE, "
              + " memberId INTEGER not NULL, "
              + " header VARCHAR(255), "
              +
              //          " response VARCHAR(255), " +
              " pointTotal INTEGER, "
              + " FOREIGN KEY ( memberId ) REFERENCES PROFILE.profileId"
              + " PRIMARY KEY ( postId ))";

      stmt.executeUpdate(sql);
      System.out.println("Created table in given database...");

    } catch (SQLException se) {
      // Handle errors for JDBC
      se.printStackTrace();
    } catch (Exception e) {
      // Handle errors for Class.forName
      e.printStackTrace();
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null) conn.close();
      } catch (SQLException se) {
      } // do nothing
      try {
        if (conn != null) conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");
  } // end main
} // end JDBCExample
