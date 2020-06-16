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

import com.google.sps.data.UserStatus;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
  private static final UserService USER = UserServiceFactory.getUserService();
  private static final Gson GSON = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("applications/json;");
    String urlToRedirectTo = "/comments.html";
    UserStatus userStatus;

    if (USER.isUserLoggedIn()) {
      String logoutUrl = USER.createLogoutURL(urlToRedirectTo);
      userStatus = new UserStatus(true, logoutUrl);
    } else {
      String loginUrl = USER.createLoginURL(urlToRedirectTo);
      userStatus = new UserStatus(false, loginUrl);
    }
    String json = GSON.toJson(userStatus);
    response.getWriter().println(json);
  }
}
