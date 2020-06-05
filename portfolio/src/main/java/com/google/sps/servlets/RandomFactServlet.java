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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random fact about me. */
@WebServlet("/random-fact")
public final class RandomFactServlet extends HttpServlet {
  private List<String> facts;

  @Override
  public void init() {
    facts = new ArrayList<>();
    facts.add("I have 4 nephews.");
    facts.add("I have been to every continent except South America and Antarctica.");
    facts.add("I am the youngest in my family by 12.5 years.");
    facts.add("I was born and raised in Regina, Saskatchewan, Canada.");
    facts.add("I have two brothers and one sister.");
    facts.add("Both my brothers are married.");
    facts.add("I have watched over 1000 episodes of anime.");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fact = facts.get((int) (Math.random() * facts.size()));

    response.setContentType("text/html;");
    response.getWriter().println(fact);
  }
}

