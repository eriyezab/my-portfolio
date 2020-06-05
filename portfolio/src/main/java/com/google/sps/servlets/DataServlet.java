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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
  private static final Query QUERY = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
  private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
  private static final Gson GSON = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PreparedQuery results = DATASTORE.prepare(QUERY);
    
    ArrayList<Comment> comments = new ArrayList<>();
    for(Entity entity: results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String message = (String) entity.getProperty("message");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, name, message, timestamp);
      comments.add(comment);
    }

    // Convert comments list to JSON
    String json = GSON.toJson(comments);

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
    long timestamp = System.currentTimeMillis();
    System.out.println("Retireved input from form.");

    // TODO: Remove later
    System.out.println("Retireved input from form.");

    // If the message was empty then do not add to datastore 
    if(message == null) {
      // TODO: Remove later
      System.out.println("Invalid message. Comment not added.");

      String queryString = "comment-posted=false";
      String url = createRedirectURL(request, queryString);
      response.sendRedirect(url);
      return;
    }

    // Create comment entity
    Entity commentEntity = createCommentEntity(name, message, timestamp);

    // Add comment entity to datastore
    DATASTORE.put(commentEntity);

    // TODO: Remove later
    System.out.println("Added comment to datastore.");

    // Redirect back to the comments page.
    response.sendRedirect("/comments.html?comment-posted=true");

    // TODO: Remove later
    System.out.println("Redirecting user.");
  }

  /**
   * Retrieve the value of the parameter from the HTTP request.
   *
   * @param request The HTTP request object
   * @param name The name of the parameter being retrieved from the request
   * @param defaultValue The value to be returned if the request parameter is null or empty
   * @return the request parameter, or the default value if the parameter
   * was not specified by the client as a string or null.
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name).strip();
    if (value == null || value == "") {
      return defaultValue;
    }
    return value;
  }

  /**
   * Create a comment entity to be inserted into the datastore.
   * 
   * @param name The name of the user who created the comment
   * @param message The content of the comment
   * @param timestamp The time the user made the comment
   * @return An instance of entity that contains the properties of the comment
   */
  private Entity createCommentEntity(String name, String message, long timestamp) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("timestamp", timestamp);
    return commentEntity;
  }

  /**
   * Create a redirect url from the request given the request and a query string to be appended.
   *
   * @param request The HTTP request object
   * @param queryString the parameters to be added to the url as a string
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
