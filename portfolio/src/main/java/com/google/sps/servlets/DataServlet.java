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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

/** Servlet that handles sending and receiving comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
  private static final Gson GSON = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get filter parameters
    int maxComments = getNumComments(request);

    Query query = makeQueryFromParams(request);
    PreparedQuery preparedQuery = DATASTORE.prepare(query);
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(maxComments);
    QueryResultList<Entity> results = preparedQuery.asQueryResultList(fetchOptions);

    ArrayList<Comment> comments = new ArrayList<>();
    for(Entity entity: results) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String message = (String) entity.getProperty("message");
      float score = ((Double) entity.getProperty("score")).floatValue();
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, name, message, score, timestamp);
      comments.add(comment);
    }

    // Convert comments list to JSON
    String json = GSON.toJson(comments);

    // Send json to server
    response.setContentType("applications/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get input from the form.
    String name = getParameter(request, "name", "Anonymous");
    String message = getParameter(request, "message", null);
    long timestamp = System.currentTimeMillis();

    // If the message was empty then do not add to datastore 
    if(message == null) {
      String queryString = "comment-posted=false";
      String url = createRedirectURL(request, queryString);
      response.sendRedirect(url);
      return;
    }

    float score = getSentimentScore(message);
    // TODO: Remove later
    // System.out.printf("'%s' was given a score of: %d\n", message, score);
    
    // Create comment entity
    Entity commentEntity = createCommentEntity(name, message, score, timestamp);

    // Add comment entity to DATASTORE
    DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
    DATASTORE.put(commentEntity);

    // Redirect back to the comments page.
    response.sendRedirect("/comments.html");
  }

  /**
   * Construct the query that retrieves comments from the datastore
   * using the parameters from the request.
   *
   * @param request The HTTP request that contains all the parameters.
   * @return a Query object that will be used to query the datastore.
   */
  private Query makeQueryFromParams(HttpServletRequest request) {
    String sortValue = getParameter(request, "sort-value", "date");
    String sortOrder = getParameter(request, "sort-order", "descending");

    String sortBy;
    if(sortValue.equals("name")) {
      sortBy = "name";
    } else {
      sortBy = "timestamp";
    }

    if(sortOrder.equals("ascending")) {
      return new Query("Comment").addSort(sortBy, SortDirection.ASCENDING);
    } else {
      return new Query("Comment").addSort(sortBy, SortDirection.DESCENDING);
    }
  }

  /**
   *
   * Create an entity to insert into datastore
   *
   * @param name The name of the user that commented.
   * @param message The message the user left.
   * @param score The sentiment score of the message.
   * @param timestamp The time the user made the comment.
   * @return an entity that can be put into the datastore containing
   * the comment information.
   */
  private Entity createCommentEntity(String name, String message, float score, long timestamp) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("score", score);
    commentEntity.setProperty("timestamp", timestamp);
    return commentEntity;
  }

  /**
   * Generate and return the sentiment score of the given message.
   *
   * @param message The comment the user made that will be analyzed.
   * @return The sentiment score that was given to the comment.
   */
  private float getSentimentScore(String message) throws IOException {
    Document doc =
              Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
    return score;
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
    String value = request.getParameter(name);
    if(value != null) {
      value = value.trim();
    }
    if (value == null || value.equals("")) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Create a redirect url from the request given the request and a query string to be appended.
   *
   * @param request The HTTP request object
   * @param queryString The parameters to be added to the url as a string
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

  /**
   * Get the max number of comments to be displayed from the request parameter
   *
   * @param request The HTTP request object
   * @return the number of comments in the request paramter, or 5 if the 
   *         parameterwas not specified by the client
   */
  private int getNumComments(HttpServletRequest request) {
    String numCommentsString = request.getParameter("num-comments");

    // Convert parameter to int
    int numComments;
    try {
      numComments = Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsString);
      return 5;
    }

    // Check that the integer is greater than or equal to 0
    if (numComments < 0 ) {
      System.err.println("Number of comments is too low: " + numCommentsString);
      return 1;
    }

    return numComments;
  }
}
