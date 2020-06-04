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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> quotes;

  @Override
  public void init(){
    quotes = new ArrayList<>();
    quotes.add("\"Good, good. This carpet is overdue for a mopping.\" - Creed Bratton");
    quotes.add("\"I'm not superstitious, but I am a little stitious.\" - Michael Scott");
    quotes.add("\"Oh you're paying way too much for worms. Who'` your worm guy?\" - Creed Bratton");
    quotes.add("\"www.creedthoughts.gov.www/creedthoughts. Check it out.\" - Creed Bratton");
    quotes.add("\"Fool me once, strike one. Fool me twice... strike three.\" - Michael Scott");
    quotes.add("\"The only problem is whenever I try to make a taco, I get too excited and crush it.\" - Kevin Malone");
    quotes.add("\"If I can't scuba, then what's this all been about? What am I working toward?\" - Creed Bratton");
    quotes.add("\"Tell him to call me ASAP as possible.\" - Michael Scott");
    quotes.add("\"I love inside jokes. I'd love to be a part of one someday.\" - Michael Scott");
    quotes.add("\"Well, well, well. How the turntables...\" - Michael Scott");
    quotes.add("\"Okay, well you're the one who lost the desk.\" - Jim Halpert");
    quotes.add("\"I had to put more and more nickels in his handset, so he would get used to the weight. "
                + "Then one day... I took 'em all out.\" - Jim Halpert");
    quotes.add("\"From time to time I send Dwight faxes. From himself. From the future.\" - Jim Halpert");
    quotes.add("\"I disagree with.\" - Jim Halpert");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String quote = quotes.get((int) (Math.random() * quotes.size()));

    response.setContentType("text/html;");
    response.getWriter().println(quote);
  }
}
