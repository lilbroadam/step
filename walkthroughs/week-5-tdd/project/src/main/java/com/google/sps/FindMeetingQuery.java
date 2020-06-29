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
import java.util.Set;
import java.util.HashSet;

public final class FindMeetingQuery {
  private static final int TIME_INCREMENT = 1;

  /**
   * Return a collection of TimeRange objects representing the available times when
   * all attendees from request are available to meet.
   * events is a collect of Events of when at least one attendee is busy. The Events
   * in events can be considered busy times when the meeting can't be scheduled.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return new ArrayList<TimeRange>();
    }

    Collection<TimeRange> meetingOptions;
    meetingOptions = busyToFree(events, request);
    meetingOptions = condenseTimeRanges(meetingOptions);
    return meetingOptions;
  }


  /**
   * Given a Collection of Events that represent when a user is unavailable, return a Collection of
   * Events representing when a user is available. Each event in the returned Collection will have
   * at least one attendee that can also be found in meetingRequest.getAttendees(). The start time
   * of Events in the Collection returned whose start times are adjacent to each other will be
   * separated by a time interval of meetingRequest.getDuration().
   */
  private static Collection<TimeRange> busyToFree(Collection<Event> busyEvents, MeetingRequest meetingRequest) {
    Collection<TimeRange> availableTimeSlots = new ArrayList<TimeRange>(busyEvents.size() / 2);
    final int eventDuration = (int)meetingRequest.getDuration();

    // For every timeslot of a day,
    for (int meetingTime = TimeRange.START_OF_DAY; meetingTime <= TimeRange.END_OF_DAY;
        meetingTime += TIME_INCREMENT) {
      // see if this time slot is a free time slot.
      boolean allCanAttend = true;
      TimeRange potentialMeeting = TimeRange.fromStartDuration(meetingTime, eventDuration);
      for (Event event : busyEvents) {
        Set<String> meetingAttendees = new HashSet<String>(meetingRequest.getAttendees());
        meetingAttendees.retainAll(event.getAttendees()); // meetingAttendees ⋂ event
        if (!meetingAttendees.isEmpty()) { // If someone from event is part of the meetingRequest.
          if (potentialMeeting.overlaps(event.getWhen())) { // This timeslot is busy; skip.
            allCanAttend = false;
            break;
          }
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
    if (expandedTimeRanges.size() == 1) {
      return expandedTimeRanges;
    }

    Collection<TimeRange> condensedTimeRanges = new ArrayList<TimeRange>();
    
    TimeRange[] timeRanges = expandedTimeRanges.toArray(new TimeRange[0]);
    for (int i = 0; i < timeRanges.length; i++) {
      TimeRange startChunk = timeRanges[i];
      for (int j = i + 1; j < timeRanges.length; j++) {
        // Start time discontinuity detected.
        if (timeRanges[j].start() != timeRanges[j - 1].start() + TIME_INCREMENT) {
          TimeRange condensed = 
            TimeRange.fromStartEnd(startChunk.start(), timeRanges[j - 1].end(), false);
          condensedTimeRanges.add(condensed);

          i = j - 1; // Cancel out the i++ at the end of the outter for loop.
          break;
        } else if (j == timeRanges.length - 1 
            || timeRanges[j].end() >= TimeRange.WHOLE_DAY.end()) {
          // Special case: last element of condensedTimeRanges.
          TimeRange condensed = 
            TimeRange.fromStartEnd(startChunk.start(), timeRanges[j - 1].end(), true);
          condensedTimeRanges.add(condensed);

          i = timeRanges.length; // End the outter for loop.
          break;
        }
      }
    }

    return condensedTimeRanges;
  }

  private static void printMeetings(String preMessage, Collection<TimeRange> meetingOptions) {
    System.out.println(preMessage);
    for(TimeRange tr : meetingOptions) {
      System.out.println(tr.toString());
    }
  }
}
