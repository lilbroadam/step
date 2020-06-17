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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['“Good, good. This carpet is overdue for a mopping.” — Creed Bratton',
      '“I’m not superstitious, but I am a little stitious.” — Michael Scott',
      '“Oh you’re paying way too much for worms. Who’s your worm guy?” – Creed Bratton',
      '“www.creedthoughts.gov.www/creedthoughts. Check it out.” – Creed Bratton',
      '“Fool me once, strike one. Fool me twice... strike three.” — Michael Scott',
      '“The only problem is whenever I try to make a taco, I get too excited and crush it.” – Kevin Malone',
      '“If I can’t scuba, then what’s this all been about? What am I working toward?” – Creed Bratton',
      '“Tell him to call me ASAP as possible.” — Michael Scott',
      '“I love inside jokes. I’d love to be a part of one someday.” — Michael Scott',
      '“Well, well, well. How the turntables...” — Michael Scott',
      '“Okay, well you’re the one who lost the desk.” — Jim Halpert',
      '“I had to put more and more nickels in his handset, so he '
        + 'would get used to the weight. Then one day… I took ‘em all out.” — Jim Halpert',
      '“From time to time I send Dwight faxes. From himself. From the future.” — Jim Halpert',
      '“I disagree with.” — Jim Halpert'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Function to get a random quote from the server
 */
async function getRandomQuote() {
  // Get the quotes in the JSON returned from the /data servlet
  fetch('/data').then(response => response.json()).then((json) => {
    const greetings = json.quotes;

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];

    // Add it to the page.
    const greetingContainer = document.getElementById('greeting-container');
    greetingContainer.innerText = greeting;
  });
}

/**
 * Fill in the 'favorite-character-votes' div with the current scoreboard
 */
async function getCharacterVotes(numCharacters) {
  // Get the favorite character votes in the JSON returned from the /data servlet
  let url = '/data?numCharacters='.concat(numCharacters);
  fetch(url).then(response => response.json()).then((json) => {
    const characterVotes = json.characterVotes;

    const greetingContainer = document.getElementById('favorite-character-votes');
    greetingContainer.innerHTML = '';

    // Populate the character scoreboard
    for(var i = 0; i < Object.keys(characterVotes).length; i++){
      greetingContainer.appendChild(createListElement(
        characterVotes[i].character + ": " + characterVotes[i].numVotes
      ));
    }
  });
}

/**
 * Creates an <li> element containing text.
 * This function was taken from 
 *   /walkthroughs/week-3-server/examples/server-stats/src/main/webapp/script.js
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Create a Google Map of UT and add it to the page. */
function loadUTMap(location, buttonHover) {
  const utCoords = {lat: 30.285, lng: -97.734};
  const towerCoords = {lat: 30.286217, lng: -97.739388};
  const utcsCoords = {lat: 30.286224, lng: -97.736531};

  // Assume map is set to overview by default
  var utMapSettings = {
    center: utCoords,
    zoom: 15
  }
  var utMap = new google.maps.Map(document.getElementById('map'), utMapSettings);
  utMap.setTilt(45);
  // TODO(adamsamuelson): have campus be highlighted by Google Maps

  var towerMarker = new google.maps.Marker({position: towerCoords, map: utMap, animation: google.maps.Animation.DROP});
  var utcsMarker = new google.maps.Marker({position: utcsCoords, map: utMap, animation: google.maps.Animation.DROP});
  setButtonHoverBounce('tower-button', towerMarker);
  setButtonHoverBounce('utcs-button', utcsMarker);

  if(location == 'tower') {
    // utMap.panTo(towerCoords); // TODO(adamsamuelson): get panTo to work
    utMap.setCenter(towerCoords);
    utMap.setZoom(18);
    utMap.setMapTypeId('satellite');
  } else if (location == 'utcs') {
    // utMap.panTo(utcsCoords); // TODO(adamsamuelson): get panTo to work
    utMap.setCenter(utcsCoords);
    utMap.setZoom(18);
    utMap.setMapTypeId('satellite');
  }
}

/** 
 * Given the elementId of a button and a marker of a Google Maps Marker,
 * set the marker to bounce whenever the button is being hovered.
 */
function setButtonHoverBounce(elementId, marker) {
  var element = document.getElementById(elementId);
  element.onmouseenter = function() {marker.setAnimation(google.maps.Animation.BOUNCE)};
  element.onmouseleave = function() {marker.setAnimation(null)};
}
