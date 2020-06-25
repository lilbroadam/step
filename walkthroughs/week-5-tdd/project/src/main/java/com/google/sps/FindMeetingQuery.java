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

    // For every time of day,
    Collection<TimeRange> availableTimeSlots = new ArrayList<TimeRange>(); // TODO change to linkedlist?
    for (int meetingTime = TimeRange.START_OF_DAY;
        meetingTime <= TimeRange.END_OF_DAY; meetingTime += TIME_INCREMENT) {
      
      boolean allCanAttend = true;
      TimeRange potentialMeeting = 
        TimeRange.fromStartDuration(meetingTime, (int)request.getDuration());

      // See if this time slot is a free time slot
      for (Event event : events) {
        if (potentialMeeting.overlaps(event.getWhen())) {
          // this timeslot isn't possible; skip this time slot
          allCanAttend = false;
          break;
        }
      }

      if (allCanAttend) {
        // save this timeslot as a possible meeting time
        availableTimeSlots.add(potentialMeeting);
      }
    }

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

  private Collection<TimeRange> condenseTimeRanges(Collection<TimeRange> expandedTimeRanges) {
    Collection<TimeRange> condensedTimeRanges = new ArrayList<TimeRange>();
    TimeRange[] timeRanges = expandedTimeRanges.toArray(new TimeRange[0]);
    for(int i = 0; i < timeRanges.length; i++) {
      TimeRange startChunk = timeRanges[i];
      System.out.println("startChunk: " + timeRanges[i].start());
      for(int j = i + 1; j < timeRanges.length; j++) {
        if( 
          (timeRanges[j].start() != timeRanges[j - 1].start() + TIME_INCREMENT) // jump detected
        //   || (timeRanges[j].end() < TimeRange.getTimeInMinutes(24, 0)) 
            ) {
          // collapse here
          TimeRange condensed = 
            TimeRange.fromStartEnd(startChunk.start(), timeRanges[j - 1].end(), false);
          condensedTimeRanges.add(condensed);

          i = j;
          break;
        } else if (
          j == timeRanges.length - 1
          || timeRanges[j].end() >= TimeRange.WHOLE_DAY.end()
            ) { // reached end

          // +1 on end of 
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

  /*
  int startTime = availableTimeSlots[0].start();
    int endTime = availableTimeSlots[0].end();
    for (TimeRange timeRange : availableTimeSlots) {
      if (TIME_INCREMENT == timeRange.start) {

      }
    }

    TimeRange chunkStart = availableTimeSlots[0];
    TimeRange chunkEnd = null;
    // for(TimeRange timeRange : availableTimeSlots){
    for(int i = 0; i < availableTimeSlots; i++) {
      TimeRange timeRange = availableTimeSlots[i];
      
      if(chunkStart.start() + TIME_INCREMENT == timeRange){

      }
    }
  */
}
