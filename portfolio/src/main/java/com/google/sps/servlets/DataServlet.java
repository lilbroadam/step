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
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> quotes;
  private Map<String, Integer> favoriteCharacterCount;
  private final String NO_FAVORITE = "No favorite";

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

    favoriteCharacterCount = new TreeMap<>();
    favoriteCharacterCount.put("Michael", 0);
    favoriteCharacterCount.put("Pam", 0);
    favoriteCharacterCount.put("Jim", 0);
    favoriteCharacterCount.put("Dwight", 0);
    favoriteCharacterCount.put("Creed", 0);
    favoriteCharacterCount.put("Angela", 0);
    favoriteCharacterCount.put("Kevin", 0);
    favoriteCharacterCount.put("Andy", 0);
    favoriteCharacterCount.put("Toby", 0);
    favoriteCharacterCount.put("Stanley", 0);
    favoriteCharacterCount.put("Ryan", 0);
    favoriteCharacterCount.put("Kelly", 0);
    favoriteCharacterCount.put("Darryl", 0);
    favoriteCharacterCount.put("Meredith", 0);
    favoriteCharacterCount.put("Oscar", 0);
    favoriteCharacterCount.put("Phyllis", 0);
    favoriteCharacterCount.put("David", 0);
    favoriteCharacterCount.put(NO_FAVORITE, 0);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = convertToJson(quotes, favoriteCharacterCount);
    
    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Parse data from the client
    String favoriteCharacter = request.getParameter("favorite-character");
    boolean noFavorite = request.getParameter("no-favorite") == null ? false : true; 

    System.out.println("preprocess:" + favoriteCharacter + " " + noFavorite);

    // Make sure only one character was listed
    if(!noFavorite && 
        (favoriteCharacter.indexOf(" ") != -1 || favoriteCharacter.indexOf(", ") != -1)){
      response.setContentType("text/html");
      response.getWriter().println("Please only enter one character's name");
      return;
    }

    // Format string so that only the first letter is capitalized
    favoriteCharacter = favoriteCharacter.substring(0, 1).toUpperCase()
                        + favoriteCharacter.substring(1, favoriteCharacter.length()).toLowerCase();

    System.out.println("postprocess:" + favoriteCharacter + " " + noFavorite);
    
    if(noFavorite){
      favoriteCharacterCount.put(NO_FAVORITE, favoriteCharacterCount.get(NO_FAVORITE) + 1);
    } else if(favoriteCharacterCount.containsKey(favoriteCharacter)){
      favoriteCharacterCount.put(favoriteCharacter, favoriteCharacterCount.get(favoriteCharacter) + 1);  
    } else {
      response.setContentType("text/html");
      response.getWriter().println("Sorry, your character wasn't recognized");
      return;        
    }

    System.out.println(favoriteCharacterCount);

    response.sendRedirect("/index.html");
  }

  private String convertToJson(List<String> officeQuotes, Map<String, Integer> characterVotes){
    String json = "{";

    json += "\"quotes\": ";
    json += new Gson().toJson(officeQuotes);
    json += ", ";
    
    json += "\"characterVotes\": ";
    json += "[";
    for(String character : characterVotes.keySet()){
      json += "{";
      json += "\"character\": ";
      json += "\"" + character + "\"";
      json += ", ";
      json += "\"numVotes\": ";
      json += "\"" + characterVotes.get(character) + "\"";
      json += "}";
      json += ", ";
    }
    json = json.substring(0, json.length() - 2); // delete the last ", ";
    json += "]";

    json += "}";
    return json;
  }
}
