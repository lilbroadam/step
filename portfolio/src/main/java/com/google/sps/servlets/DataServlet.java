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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

/* Servlet that returns quotes from The Office and handles favorite character votes. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> quotes;
  private Set<String> characterNamesSet;
  private static final String NO_FAVORITE = "No favorite";
  private static int num_characters_display = 10;

  @Override
  public void init() {
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

    // Populate characterNamesSet to quickly identify if a name is a character's name or not
    characterNamesSet = new HashSet<>();
    try {
      Scanner characterNamesScanner = new Scanner(new File("./character_names.txt"));
      while(characterNamesScanner.hasNext()){
        characterNamesSet.add(characterNamesScanner.nextLine());
      }
    } catch (FileNotFoundException e) {
      System.out.println("WARNING: character_names.txt couldn't be found");
    }
  }

  /**
   * Return a JSON that contains a .quotes array of quotes from The Office
   * and a .characterVotes array of objects that are character:numVotes pairs.
   * Optional parameter: numCharacters - set the number of character:numVotes objects
   * to be returned in the JSON.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String numCharactersParameter = request.getParameter("numCharacters");
    if(numCharactersParameter != null){
      num_characters_display = Integer.parseInt(numCharactersParameter);
    }
    
    String json = convertToJson(quotes, num_characters_display);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Update the datastore with the client's vote for their favorite character.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Parse data from the client's request
    String favoriteCharacter = request.getParameter("favorite-character");
    boolean noFavorite = request.getParameter("no-favorite") != null;

    // Pre-process client's request
    if(!noFavorite){
      // Make sure only one character was listed
      if(favoriteCharacter.split(",|\\ ").length != 1) {
        response.setContentType("text/html");
        response.getWriter().println("Please only enter one character's name");
        return;
      }

      // Format string so that only the first letter is capitalized
      favoriteCharacter = favoriteCharacter.substring(0, 1).toUpperCase()
                          + favoriteCharacter.substring(1).toLowerCase();
      
      if(!characterNamesSet.contains(favoriteCharacter)){
        response.setContentType("text/html");
        response.getWriter().println("Sorry, your character wasn't recognized");
        return;
      }
    } else {
      favoriteCharacter = NO_FAVORITE;
    }

    // Update the datastore with the new vote
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Key characterKey = KeyFactory.createKey("characterVotes", favoriteCharacter);
      Entity characterEntity = datastore.get(characterKey);
      int currentNumVotes = (int)(long) characterEntity.getProperty("numVotes");
      characterEntity.setProperty("numVotes", currentNumVotes + 1);
      datastore.put(characterEntity);
    } catch (Exception e) {
      response.setContentType("text/html");
      response.getWriter().println("Sorry, there was an error while updating the server");
      return;
    }

    response.sendRedirect("/index.html");
  }

  /**
   * Return a JSON that contains a .quotes array of quotes from The Office
   * and a .characterVotes array of objects that are character:numVotes pairs.
   * The number of objects in the .characterVotes array will not exceed numCharacters.
   * TODO(adamsamuelson): when numCharacters imposes a limit on the json, return the top-voted characters
   * TODO(adamsamuelson): have quotes be read in from a txt file
   */
  private String convertToJson(List<String> officeQuotes, int numCharacters) {
    String json = "{";

    json += "\"quotes\": ";
    json += new Gson().toJson(officeQuotes);
    json += ", ";
    
    json += "\"characterVotes\": ";
    json += "[";
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(new Query("characterVotes"));
    for(Entity entity : results.asIterable()){
      if(numCharacters <= 0) // limit the number of characters returned
        break;

      json += "{";
      json += "\"character\": ";
      json += "\"" + entity.getKey().getName() + "\"";
      json += ", ";
      json += "\"numVotes\": ";
      json += "\"" + entity.getProperty("numVotes") + "\"";
      json += "}";
      json += ", ";

      numCharacters--;
    }

    json = json.substring(0, json.length() - 2); // delete the last ", "
    json += "]";

    json += "}";
    return json;
  }
}
