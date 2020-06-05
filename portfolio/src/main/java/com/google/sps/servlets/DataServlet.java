// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<Comment> comments = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Convert comments list to JSON
    Gson gson = new Gson();
    String json = gson.toJson(comments);

    // Send json to server
    response.setContentType("applications/json;");
    response.getWriter().println(json);

    // TODO: Remove later
    System.out.println("Sent json to client.");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get input from the form.
    String name = getParameter(request, "name", "Anonymous");
    String message = getParameter(request, "message", null);

    // TODO: Remove later
    System.out.println("Retireved input from form.");

    // If the message was empty then do not add to list 
    if(message == null) {
      // TODO: Remove later
      System.out.println("Invalid message. Comment not added.");

      String queryString = "comment-posted=false";
      String url = createRedirectURL(request, queryString);
      response.sendRedirect(url);
      return;
    }

    // Add the comment to the arraylist.
    Comment newComment = new Comment(name, message, System.currentTimeMillis());
    comments.add(newComment);

    // TODO: Remove later
    System.out.println("Added comment to list.");

    // Redirect back to the comments page.
    response.sendRedirect("/comments.html?comment-posted=true");

    // TODO: Remove later
    System.out.println("Redirecting user.");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   * was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name).strip();
    if (value == null || value == "") {
      return defaultValue;
    }
    return value;
  }

  /**
   * Create a redirect url from the request given the request and a query string to be appended.
   *
   * @return the constructed url as a string
   */
  private String createRedirectURL(HttpServletRequest request,  String queryString) {
    String fullQueryString;
    if(request.getQueryString() == null) {
      fullQueryString = queryString;
    } else {
      fullQueryString = request.getQueryString() + "&" + queryString;
    }

    String redirectURL = "/comments.html?" + fullQueryString;
    return redirectURL;
  }
}
