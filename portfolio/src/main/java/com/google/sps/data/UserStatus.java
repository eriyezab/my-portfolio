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

package com.google.sps.data;

/**
 * Class representing the logged in status of the user.
 *
 * <p>Note: The private variables in this class are converted into JSON.
 */
public class UserStatus {

  /** The logged in status of the user */
  private boolean status;

  /** The url that the user clicks to log in or log out depending on status. */
  private String url;

  public UserStatus(boolean status, String url) {
    this.status = status;
    this.url = url;
  }
  
  public boolean getStatus() {
    return this.status;
  }

  public String getUrl() {
    return this.url;
  }
}
