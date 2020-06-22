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

  /**
   * Takes a collection of events and a MeetingRequest and finds all TimeRanges with a duration
   * greater to or equal to that of in the request that allows for all mandatory and some or no
   * optional attendees to attend.
   *
   * @param events The list of events that are happening throughout the day.
   * @param request The meeting request.
   * @return A collection of TimeRanges that allows for all mandatory attendees to attend the 
   * meeting which includes optional attendees if possible.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Create arrays that will hold all the free and busy meeting time ranges. 
    ArrayList<TimeRange> freeTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> optionalFreeTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> busyTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> optionalBusyTimes = new ArrayList<TimeRange>();

    // Add each event's time range to busyTimes list if one of the attendees in the request is in that event.
    Collection<String> mandatoryAttendees = request.getAttendees();
    getBusyTimeRanges(busyTimes, events, mandatoryAttendees);

    // Add each event's time range to optionalBusyTimes if one of the optional attendees in the request is in that event.
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    getBusyTimeRanges(optionalBusyTimes, events, optionalAttendees);

    // If there are no times when attendees have meetings, check if the duration is less than 24 hrs and
    // if so then return a time range encompassing the entire day.
    if (busyTimes.size() == 0 && optionalBusyTimes.size() == 0) {
      TimeRange wholeDay = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
      if (wholeDay.duration() >= request.getDuration()) {
        freeTimes.add(wholeDay);
      }
      return freeTimes;
    } else if (busyTimes.size() == 0) { // If there are no mandatory attendees but some optional attendees.
      Collections.sort(optionalBusyTimes, TimeRange.ORDER_BY_START);
      removeOverlaps(optionalBusyTimes);
      findFreeTimes(optionalBusyTimes, optionalFreeTimes, request.getDuration());
      return optionalFreeTimes;
    } else if (optionalBusyTimes.size() != 0) { // If there are some optional attendees and some mandatory attendees.
      Collections.sort(busyTimes, TimeRange.ORDER_BY_START);
      Collections.sort(optionalBusyTimes, TimeRange.ORDER_BY_START);
      // Need to filter the busyTimes list so that all overlaps are removed and the list is modified accordingly.
      removeOverlaps(busyTimes);
      removeOverlaps(optionalBusyTimes);

      // Add each opening from the busyTimes list to freeTimes list if the opening's duration is greater than or 
      // equal to the meeting request's duration.
      findFreeTimes(busyTimes, freeTimes, request.getDuration());
      findFreeTimes(optionalBusyTimes, optionalFreeTimes, request.getDuration());

      // Return the merged free times list if there are any times that work for all mandatory attendees and some
      // optional attendees. If no times work for the optional attendees then return the original free times list
      // that contains all the free times for the mandatory attendees.
      ArrayList<TimeRange> mergedFreeTimes = mergeFreeTimes(freeTimes, optionalFreeTimes, request.getDuration());
      if(mergedFreeTimes.size() > 0) {
        return mergedFreeTimes;
      } else {
        return freeTimes;
      }
    } else { // If there are some mandatory attendees but no optional attendees.
      removeOverlaps(busyTimes);
      findFreeTimes(busyTimes, freeTimes, request.getDuration());
      return freeTimes;
    }
  }

  /**
   * Add the TimeRange to the times list if and only if the TimeRange has a duration of
   * at least duration.
   *
   * @param range The TimeRange to be added to times.
   * @param times The list of TimeRanges that range will be added to.
   * @param duration The duration the TimeRange has to be greater than to be added to times.
   */
  private void addTimeRange(TimeRange range, ArrayList<TimeRange> times, long duration) {
    if (range.duration() >= duration) {
      times.add(range);
    }
  }

  /** 
   * Add each event's TimeRange to busyTimes if they have attendees that are in the meeting request's
   * attendees collection.
   *
   * @param busyTimes The list that each event's TimeRange will be added to.
   * @param events A collection of events whose TimeRange's will be added to busyTimes.
   * @param attendees A collection of attendees that are attending the meeting.
   */
  private void getBusyTimeRanges(ArrayList<TimeRange> busyTimes, Collection<Event> events, Collection<String> attendees) {
    for (Event event: events) {
      if (!(Collections.disjoint(event.getAttendees(), attendees))) {
        busyTimes.add(event.getWhen());
      }
    }
  }

  /**
   * Replaces any overlapping TimeRanges with a single TimeRange that encompasses them all.
   *
   * @param times A list of TimeRanges sorted by their start time.
   */
  private void removeOverlaps(ArrayList<TimeRange> times) {
    int i = 0;
    while (i < times.size() - 1) {
      if (times.get(i).overlaps(times.get(i+1))) {
        if (!(times.get(i).contains(times.get(i+1)))) {
          times.set(i, TimeRange.fromStartEnd(times.get(i).start(), times.get(i+1).end(), false));
        }
        times.remove(i+1);
      } else {
        ++i;
      }
    }
  }

  /**
   * Takes a list of busyTimes and creates a complementing list of freeTimes that contains the gaps between
   * the TimeRanges in busyTimes.
   * @param busyTimes A sorted list of TimeRanges that represents all times that are not free.
   * @param freeTimes An empty list that will have TimeRanges representing free slots added to it.
   * @param meetingDuration The minimum TimeRange duration that can be added to freeTimes.
   */
  private void findFreeTimes(ArrayList<TimeRange> busyTimes, ArrayList<TimeRange> freeTimes, long meetingDuration) {
    int numBusyTimes = busyTimes.size();
    if (!(busyTimes.get(0).contains(TimeRange.START_OF_DAY))) {
      TimeRange first = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(), false);
      addTimeRange(first, freeTimes, meetingDuration);
    }

    for (int i = 0; i < numBusyTimes - 1; ++i) {
      TimeRange range = TimeRange.fromStartEnd(busyTimes.get(i).end(), busyTimes.get(i+1).start(), false);
      addTimeRange(range, freeTimes, meetingDuration);
    }

    if (!(busyTimes.get(numBusyTimes - 1).contains(TimeRange.END_OF_DAY))) {
      TimeRange last = TimeRange.fromStartEnd(busyTimes.get(numBusyTimes - 1).end(), TimeRange.END_OF_DAY, true);
      addTimeRange(last, freeTimes, meetingDuration);
    }
  }

  /**
   * Merges the TimeRanges in optionalFreeTimes with TimeRanges in freeTimes to create a list of TimeRanges
   * that allows for both mandatory and optional attendees to attend.
   *
   * @param freeTimes A sorted list of TimeRanges representing the available times for mandatory attendees.
   * @param optionalFreeTimes A sorted list of TimeRanges representing the available times for optional attendees.
   * @param meetingDuration The minimum duration a TimeRange can be if added to the merged list.
   * @return A list of TimeRanges that allow for both mandatory and optional attendees to attend the meeting.
   */
  private ArrayList<TimeRange> mergeFreeTimes(ArrayList<TimeRange> freeTimes, ArrayList<TimeRange> optionalFreeTimes, long meetingDuration) {
    ArrayList<TimeRange> mergedFreeTimes = new ArrayList<>();
    int freeIndex = 0;
    int optionalIndex = 0;
    int sizeFree = freeTimes.size();
    int sizeOptional = optionalFreeTimes.size();
    
    while (freeIndex < sizeFree && optionalIndex < sizeOptional) {
      TimeRange optionalTime = optionalFreeTimes.get(optionalIndex);
      TimeRange freeTime = freeTimes.get(freeIndex);
      if (optionalTime.overlaps(freeTime)) {
        int start = (optionalTime.start() > freeTime.start() ? optionalTime.start() : freeTime.start());
        int end = (optionalTime.end() < freeTime.end() ? optionalTime.end() : freeTime.end());
        TimeRange range = TimeRange.fromStartEnd(start, end, false);
        addTimeRange(range, mergedFreeTimes, meetingDuration);
        ++optionalIndex;
      } else {
        ++freeIndex;
      }
    }

    return mergedFreeTimes;
  }
}
