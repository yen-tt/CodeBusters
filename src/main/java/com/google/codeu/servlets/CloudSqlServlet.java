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
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.stream.Collectors;
import com.google.gson.*;

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

    String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JsonParser parse = new JsonParser();
    JsonObject jObj = (JsonObject)parse.parse(requestBody);

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }


    if (req.getRequestURI().startsWith("/question")) {
      // Perform get Question.
      final String selectQuestionSql =
          "SELECT postId " + "FROM POST " + "WHERE header = '" + jObj.get("header") + "'";

      try (ResultSet rs = conn.prepareStatement(selectQuestionSql).executeQuery()) {
        System.out.println("Query successfully completed");
        String queryResult = rs.getString(1);
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/plain");
        writer.println(queryResult);
      } catch (SQLException e) {
        throw new ServletException("SQL GetQuestion error", e);
      }
    } else if (req.getRequestURI().startsWith("/member")) {
      // Perform get Member.
      final String selectMemberSql =
          "SELECT profileId " + "FROM PROFILE " + "WHERE emailP = '" + jObj.get("emailP") + "'";

      try (ResultSet rs = conn.prepareStatement(selectMemberSql).executeQuery()) {
        System.out.println("Query successfully completed");
        String queryResult = rs.getString(1);
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/plain");
        writer.println(queryResult);
      } catch (SQLException e) {
        throw new ServletException("SQL GetMember error", e);
      }
    } else if (req.getRequestURI().startsWith("/login")) {
      // Perform get Member.
      final String selectMemberSql =
          "SELECT emailU " + "FROM USER " + "WHERE emailU = '" + jObj.get("emailU") + "'";

      try (ResultSet rs = conn.prepareStatement(selectMemberSql).executeQuery()) {
        System.out.println("Query successfully completed");
        String queryResult = rs.getString(1);
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/plain");
        if(queryResult.isEmpty()) {
          writer.println(false);  //if user non-existent
        }else {
          writer.println(true);   //if user existent
        }
      } catch (SQLException e) {
        throw new ServletException("SQL GetUser error", e);
      }
    }else if (req.getRequestURI().startsWith("/reply")) {
      // Perform get Member.
      final String selectPostSql =
          "SELECT postIdP " + "FROM POST " + "WHERE header = '" + jObj.get("header") + "'";

      String queryResult;
      try (ResultSet rs = conn.prepareStatement(selectPostSql).executeQuery()) {
        System.out.println("Query successfully completed");
        queryResult = rs.getString(1);
      } catch (SQLException e) {
        throw new ServletException("SQL GetPostId-Answer error", e);
      }

      final String selectAnswerSql =
          "SELECT answer " + "FROM ANSWER " + "WHERE postIdA = '" + queryResult + "'";

      try (ResultSet rs = conn.prepareStatement(selectPostSql).executeQuery()) {
        System.out.println("Query successfully completed");
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/plain");
        String queryAnswerResult = rs.getString(1);
      } catch (SQLException e) {
        throw new ServletException("SQL GetAnswer error", e);
      }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JsonParser parse = new JsonParser();
    JsonObject jObj = (JsonObject)parse.parse(requestBody);


    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }

      if(req.getRequestURI().startsWith("/reply")){
      // Perform save Answer.
      final String createAnswerSql =
          "INSERT INTO ANSWERS (postId, answer) "
              + "VALUES ( "
//              + req.body.postID
              + jObj.get("postID")
              + ", "
//              + req.body.answer
              + jObj.get("answer")
              + " )";

      try (PreparedStatement statementCreateVisit = conn.prepareStatement(createAnswerSql)) {
        System.out.println("Query successfully completed");
        conn.createStatement().executeUpdate(createAnswerSql);

      } catch (SQLException e) {
        throw new ServletException("SQL CreateReply error", e);
      }

      } else if (req.getRequestURI().startsWith("/question")) {
      // Perform save Question.
      final String createQuestionSql =
          "INSERT INTO POST (memberId, header, pointTotal) "
              + "VALUES ( "
              + jObj.get("memberId")
              + ", "
              + jObj.get("header")
              + ", "
              + jObj.get("pointTotal")
              + " )";

      try (PreparedStatement statementCreateVisit = conn.prepareStatement(createQuestionSql)) {
        System.out.println("Query successfully compiled");
        conn.createStatement().executeUpdate(createQuestionSql);

      } catch (SQLException e) {
        throw new ServletException("SQL CreateQuestion error", e);
      }
    }
  }

  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JsonParser parse = new JsonParser();
    JsonObject jObj = (JsonObject)parse.parse(requestBody);

    String path = req.getRequestURI();
    if (path.startsWith("/favicon.ico")) {
      return; // ignore the request for favicon.ico
    }

    if (req.getRequestURI().startsWith("/reply")) {
      // Perform save Answer.
      final String updatePointsSql =
          "UPDATE POST "
              + "SET ( pointTotal = "
              + jObj.get("pointTotal")
              + " )"
              + "WHERE postId = "
              + jObj.get("postId");

      try (PreparedStatement statementCreateVisit = conn.prepareStatement(updatePointsSql)) {
        System.out.println("Query successfully compiled");
        conn.createStatement().executeUpdate(updatePointsSql);

      } catch (SQLException e) {
        throw new ServletException("SQL UpdatePoint error", e);
      }
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

    final String createUserTableSql =
        "CREATE TABLE IF NOT EXISTS USER ( "
            + "emailU NVARCHAR NOT NULL, "
//            + "password NVARCHAR NOT NULL"
            + "PRIMARY KEY (emailU) );";
    final String createProfileTableSql =
        "CREATE TABLE IF NOT EXISTS PROFILE ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED, "
            + "profileId AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED, "
            + "first VARCHAR NOT NULL, "
            + "last VARCHAR NOT NULL,  isCPA BIT NOT NULL, "
            + "emailP NVARCHAR NOT NULL, "
            + "FOREIGN KEY ( emailP ) REFERENCES USER.emailU, PRIMARY KEY (profileId) );";
    final String createPostTableSql =
        "CREATE TABLE IF NOT EXISTS POST ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED,"
            + "postIdP AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED,"
            + "memberId SERIAL NOT NULL, "
            + "header VARCHAR,  pointTotal INTEGER, "
            + "FOREIGN KEY ( memberId ) REFERENCES PROFILE.profileId, PRIMARY KEY (postId) );";
    final String createAnswerTableSql =
        "CREATE TABLE IF NOT EXISTS ANSWERS ( "
            + "ID INT IDENTITY(1,1) NOT NULL PRIMARY KEY CLUSTERED,"
            + "answerId AS 'UID' + RIGHT('00000000' + CAST(ID AS VARCHAR(8)), 8) PERSISTED,"
            + "postIdA SERIAL NOT NULL, "
            + "answer VARCHAR, "
            + "FOREIGN KEY ( postIdA ) REFERENCES POST.postIdP, PRIMARY KEY (answerId) );";

    try(PreparedStatement statementUserVisit = conn.prepareStatement(createUserTableSql)){
      conn.createStatement().executeUpdate(createUserTableSql);
    }catch(SQLException e) {
      throw new ServletException("SQL UserTable error", e);
    }

    try(PreparedStatement statementProfileVisit = conn.prepareStatement(createProfileTableSql)){
      conn.createStatement().executeUpdate(createProfileTableSql);
    }catch(SQLException e) {
      throw new ServletException("SQL ProfileTable error", e);
    }

    try(PreparedStatement statementPostVisit = conn.prepareStatement(createPostTableSql)){
      conn.createStatement().executeUpdate(createPostTableSql);
    }catch(SQLException e) {
      throw new ServletException("SQL PostTable error", e);
    }

    try(PreparedStatement statementAnswerVisit = conn.prepareStatement(createAnswerTableSql)){
      conn.createStatement().executeUpdate(createAnswerTableSql);
    }catch(SQLException e) {
      throw new ServletException("SQL AnswerTable error", e);
    }

  }
}
// [END gae_java8_mysql_app]
