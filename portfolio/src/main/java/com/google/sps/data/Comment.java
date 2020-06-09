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
 * Class representing a comment that the users create.
 *
 * <p>Note: The private variables in this class are converted into JSON.
 */
public class Comment {

  /** The id of the comment in datastore. */
  private long id;

  /** The name of the user who commented. */
  private String name;

  /** The email of the user who commented. */
  private String email;

  /** The message that the user commented. */
  private String message;

  /** The time the user made the comment. */
  private long timestamp;
  
  public Comment(long id, String name, String email, String message, long timestamp) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.message = message;
    this.timestamp = timestamp;
  }
  
  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  } 

  public String getEmail() {
    return this.email;
  }

  public String getMessage() {
    return this.email;
  }

  public long timestamp() {
    return this.timestamp;
  }


}
