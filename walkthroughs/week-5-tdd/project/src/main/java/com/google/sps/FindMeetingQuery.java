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

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Create arrays to hold all the times attendees have meetings and the free times of all attendees
    ArrayList<TimeRange> freeTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> busyTimes = new ArrayList<TimeRange>();

    // Add each event's time range to busyTimes list if one of the attendees in the request is in that event
    for (Event event: events) {
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      Set<String> requestAttendees = new HashSet<String>(request.getAttendees());
      if (eventAttendees.removeAll(requestAttendees)) {
        busyTimes.add(event.getWhen());
      }
    }

    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

    // If there are no times when attendees have meetings, check if the duration is less than 24 hrs and
    // if so then return a time range encompassing the entire day
    if (busyTimes.size() == 0) {
      TimeRange wholeDay = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
      if (wholeDay.duration() >= request.getDuration()) {
        freeTimes.add(wholeDay);
      }
      return freeTimes;
    }

    // Need to filter the busyTimes list so that all overlaps are removed and the list is modified accordingly
    int i = 0;
    while (i < busyTimes.size() - 1) {
      if (busyTimes.get(i).overlaps(busyTimes.get(i+1))) {
        if (!(busyTimes.get(i).contains(busyTimes.get(i+1)))) {
          busyTimes.set(i, TimeRange.fromStartEnd(busyTimes.get(i).start(), busyTimes.get(i+1).end(), false));
        }
        busyTimes.remove(i+1);
      } else {
        ++i;
      }
    }

    int sizeBusyTimesList = busyTimes.size();

    // Add each opening from the busyTimes list to freeTimes list if the opening's duration is greater than or 
    // equal to the meeting request's duration
    if (sizeBusyTimesList > 0) {
      if (!(busyTimes.get(0).contains(TimeRange.START_OF_DAY))) {
        TimeRange first = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(), false);
        if (first.duration() >= request.getDuration()) {
          freeTimes.add(first);
        }
      }
    }

    for (i = 0; i < sizeBusyTimesList - 1; ++i) {
      TimeRange range = TimeRange.fromStartEnd(busyTimes.get(i).end(), busyTimes.get(i+1).start(), false);
      if (range.duration() >= request.getDuration()) {
        freeTimes.add(range);
      }
    }

    if (sizeBusyTimesList > 0) {
      if (!(busyTimes.get(sizeBusyTimesList - 1).contains(TimeRange.END_OF_DAY))) {
        TimeRange last = TimeRange.fromStartEnd(busyTimes.get(sizeBusyTimesList - 1).end(), TimeRange.END_OF_DAY, true);
        if (last.duration() >= request.getDuration()) {
          freeTimes.add(last);
        }
      }
    }

    return freeTimes;
  }
}
