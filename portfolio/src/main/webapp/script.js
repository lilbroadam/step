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
async function getRandomQuote(){
  const response = await fetch('/data');
  const quote = await response.text();
  document.getElementById('greeting-container').innerText = quote;
}