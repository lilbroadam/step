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
import java.util.ArrayList;

public final class FindMeetingQuery {
  private static final int TIME_INCREMENT = 1;

  /**
   * Return a collection of TimeRange objects representing the available times when
   * all attendees from request are available to meet.
   * events is a collect of Events of when at least one attendee is busy. The Events
   * in events can be considered busy times when the meeting can't be scheduled.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    /**
     * Instructions: 
     * For a time slot to work, all attendees must be free to attend the meeting.
     * When a query is made, it will be given a collection of all known events.
     * Each event has:
     * - a name
     * - a time range
     * - a collection of attendees
    */

    Collection<TimeRange> availableTimeSlots = busyToFree(events, (int)request.getDuration());

    System.out.println(request.hashCode() + ": " + request.getDuration() + "; " + request.getAttendees().toString());
    for(Event event : events) {
      // System.out.println(event.getTitle() + ": " + event.getWhen().duration());
      System.out.println(event.getWhen().start() + " - " + event.getWhen().end() + "; " + event.getAttendees());
    }
    
    // Condense the available meeting times into one TimeRange for each time chunk
    System.out.println("Before condensing:");
    for (TimeRange time : availableTimeSlots) {
      System.out.println(time.start() + " - " + time.end());
    }
    availableTimeSlots = condenseTimeRanges(availableTimeSlots);
    System.out.println("After condensing:");
    for (TimeRange time : availableTimeSlots) {
      System.out.println(time.start() + " - " + time.end());
    }
    System.out.println();


    return availableTimeSlots;
  }

  /**
   * Given a Collection of Events that represent when a user is unavailable, return a Collection of
   * Events representing when a user is available. The start time of Events in the Collection whose
   * start times are adjacent to each other will be separated by a time interval of eventDuration.
   */
  private static Collection<TimeRange> busyToFree(Collection<Event> busyEvents, int eventDuration) {
    Collection<TimeRange> availableTimeSlots = new ArrayList<TimeRange>(busyEvents.size() / 2);

    // For every timeslot of a day,
    for (int meetingTime = TimeRange.START_OF_DAY; meetingTime <= TimeRange.END_OF_DAY;
        meetingTime += TIME_INCREMENT) {
      // see if this time slot is a free time slot.
      boolean allCanAttend = true;
      TimeRange potentialMeeting = TimeRange.fromStartDuration(meetingTime, eventDuration);
      for (Event event : busyEvents) {
        if (potentialMeeting.overlaps(event.getWhen())) { // This timeslot is busy; skip.
          allCanAttend = false;
          break;
        }
      }

      if (allCanAttend) {
        availableTimeSlots.add(potentialMeeting);
      }
    }

    return availableTimeSlots;
  }

  /**
   * Given a Collection of TimeRanges, return a Collection of condensed TimeRanges.
   * Ex: [[0, 30], [1, 31], [2, 32], [60, 90]] -> [[0, 32], [60, 90]]
   */
  private static Collection<TimeRange> condenseTimeRanges(Collection<TimeRange> expandedTimeRanges) {
    Collection<TimeRange> condensedTimeRanges = new ArrayList<TimeRange>(); // TODO change to linkedlist?
    
    TimeRange[] timeRanges = expandedTimeRanges.toArray(new TimeRange[0]);
    for(int i = 0; i < timeRanges.length; i++) {
      TimeRange startChunk = timeRanges[i];
      System.out.println("startChunk: " + timeRanges[i].start());
      for(int j = i + 1; j < timeRanges.length; j++) {
        // Start time jump detected
        if(timeRanges[j].start() != timeRanges[j - 1].start() + TIME_INCREMENT) {
          TimeRange condensed = 
            TimeRange.fromStartEnd(startChunk.start(), timeRanges[j - 1].end(), false);
          condensedTimeRanges.add(condensed);

          i = j - 1; // cancel out the i++ at the end of the outter for loop
          break;
        } else if (j == timeRanges.length - 1
            || timeRanges[j].end() >= TimeRange.WHOLE_DAY.end()) { // tail of condensedTimeRanges

          TimeRange condensed = 
            TimeRange.fromStartEnd(startChunk.start(), timeRanges[j - 1].end(), true);
          condensedTimeRanges.add(condensed);
          i = timeRanges.length;
          break;
        }
      }
    }

    return condensedTimeRanges;
  }
}
