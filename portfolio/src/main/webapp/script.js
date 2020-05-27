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
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
      ['I am the youngest in my family by 12.5 years.', 'I have 4 nephews.', 'I have been to every continent except South America and Antarctica.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

function audioPlayer() {
  console.log('Running audio player.');
  
  // First song in the list is set as the current song.
  currentSong = 0;
  
  player = document.getElementById('audio-player');
  songs = document.querySelectorAll('#song-list li a');
  
  // Modifying the click event for the links to play the song.
  songs.forEach((song, index, songList) => {
    song.addEventListener('click', (event) => {
      console.log('Clicked song.');
      
      // Default behaviour of links takes client to another page which is unwanted.
      event.preventDefault();
      
      // Change the previously played song classname to be empty as it is no longer the current song.
      songList[currentSong].parentNode.className = '';
      
      // Play the current song and add a class of current-song for styling
      song.parentNode.className = 'current-song';
      player.src = song.href;
      currentSong = index;
      player.play();
    }, false);
  });
}

audioPlayer();


