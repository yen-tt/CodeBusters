package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/home.html")
public class HomeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    response.setContentType("text/html;");
    response.getOutputStream().println("<!DOCTYPE html>");
    response.getOutputStream().println("<html>");
    response.getOutputStream().println("<head>");
    response.getOutputStream().println("<title>Servlet HTML Example</title>");
    response.getOutputStream().println("</head>");
    response.getOutputStream().println("<body>");

    if (userService.isUserLoggedIn()) {
      String username = userService.getCurrentUser().getEmail();
      response
          .getOutputStream()
          .println("<a href=\"/user-page.html?user=" + username + "\">Your Page</a>");
      response.getOutputStream().println("<a href=\"/logout\">Logout</a>");
    } else {
      response.getOutputStream().println("<a href=\"/login\">Login</a>");
    }

    response.getOutputStream().println("<h1>Servlet HTML Example</h1>");
    response.getOutputStream().println("<p>This HTML came from a servlet!</p>");
    response.getOutputStream().println("</body>");
    response.getOutputStream().println("</html>");
  }
}
