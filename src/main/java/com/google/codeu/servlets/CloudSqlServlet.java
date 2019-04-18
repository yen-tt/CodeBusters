/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.cloudsql;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START gae_java8_mysql_app]
@SuppressWarnings("serial")
// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
  name = "CloudSQL",
  description = "CloudSQL: Store tax forum information",
  urlPatterns = "/cloudsql"
)
public class CloudSqlServlet extends HttpServlet {
  Connection conn;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }

    PrintWriter out = resp.getWriter();
    resp.setContentType("text/plain");

    if (request.getParameter("getQuestion") != null) {
      // Perform get Question.
      final String selectQuestionSql = "SELECT postId "
          + "FROM POST "
          + "WHERE header = '" + req.body.header + "'";

      try(ResultSet rs = conn.prepareStatement(selectQuestionSql).executeQuery()){
          System.out.println("Query successfully completed");
          String queryResult = rs.getString(1);
          resp.body.queryResult = queryResult;

      }catch(SQLException e){
          throw new ServletException("SQL error", e);
      }
    }

  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }


    if (request.getParameter("saveAnswer") != null) {
      // Perform save Answer.
      final String createAnswerSql =
          "INSERT INTO ANSWERS (postId, answer) " +
          "VALUES ( " +
          req.body.postID + ", " +
          req.body.answer + " )";

      try(ResultSet rs = conn.prepareStatement(createAnswerSql)){
        System.out.println("Query successfully completed");
        conn.createStatement().executeUpdate(createAnswerSql);

      }catch(SQLException e){
        throw new ServletException("SQL error", e);
      }

    }
    else if (request.getParameter("saveQuestion") != null) {
      // Perform save Question.
      final String createQuestionSql =
          "INSERT INTO POST (memberId, header, pointTotal) " +
              "VALUES ( " +
              req.body.memberId + ", " +
              req.body.header + ", " +
              req.body.pointTotal + " )";

      try(ResultSet rs = conn.prepareStatement(createQuestionSql)){
        System.out.println("Query successfully compiled");
        conn.createStatement().executeUpdate(createQuestionSql);

      }catch(SQLException e){
        throw new ServletException("SQL error", e);
      }

    }



  }

  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }


    if (request.getParameter("updatePoints") != null) {
      // Perform save Answer.
      final String updatePointsSql =
          "UPDATE POST " +
              "SET ( pointTotal = " +
              req.body.pointTotal + " )" +
              "WHERE postId = " +
              req.body.postId;
    }

    try(ResultSet rs = conn.prepareStatement(updatePointsSql)){
      System.out.println("Query successfully compiled");
      conn.createStatement().executeUpdate(updatePointsSql);

    }catch(SQLException e){
      throw new ServletException("SQL error", e);
    }



  }

  @Override
  public void init() throws ServletException {
    String url = System.getProperty("cloudsql");
    log("connecting to: " + url);
    try {
      conn = DriverManager.getConnection(url);
    } catch (SQLException e) {
      throw new ServletException("Unable to connect to Cloud SQL", e);
    }

    final String createProfileTableSql =
        "CREATE TABLE IF NOT EXISTS PROFILE ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED, "
            + "profileId AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED, "
            + "first VARCHAR NOT NULL, "
            + "last VARCHAR NOT NULL,  isCPA BIT not NULL, "
            + "PRIMARY KEY (profileId) );";
    final String createPostTableSql =
        "CREATE TABLE IF NOT EXISTS POST ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED,"
            + "postId AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED,"
            + "memberId SERIAL NOT NULL, "
            + "header VARCHAR,  pointTotal INTEGER, "
            + " FOREIGN KEY ( memberId ) REFERENCES PROFILE.profileId, PRIMARY KEY (postId) );";
    final String createAnswerTableSql =
        "CREATE TABLE IF NOT EXISTS ANSWERS ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED,"
            + "answerId AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED,"
            + "postId SERIAL NOT NULL, "
            + "answer VARCHAR, "
            + " FOREIGN KEY ( postId ) REFERENCES POST.postId, PRIMARY KEY (answerId) );";

    conn.createStatement().executeUpdate(createProfileTableSql);
    conn.createStatement().executeUpdate(createPostTableSql);
    conn.createStatement().executeUpdate(createAnswerTableSql);
  }
}
// [END gae_java8_mysql_app]
