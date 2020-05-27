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

  // Pick a random greeting.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

function audioPlayer() {
  console.log('Running audio player.');
  currentSong = 0;
  player = document.getElementById('audio-player');
  songs = document.querySelectorAll('#song-list li a');
  console.log('Adding Event Listener to song links...');
  songs.forEach((song, index, songList) => {
    song.addEventListener('click', (event) => {
      console.log('Clicked song.');
      event.preventDefault();
      songList[currentSong].parentNode.className = '';
      player.src = song.href;
      song.parentNode.className = 'current-song';
      currentSong = index;
    }, false);
  });
}

audioPlayer();


