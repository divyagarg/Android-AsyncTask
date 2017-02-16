package com.example.divgarg.calendarutil;

/**
 * Created by divgarg on 2/9/17.
 */

public class EventAttendee {
    private Long eventId;
    private String AttendeeEmail;
    private String AttendeeType;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getAttendeeEmail() {
        return AttendeeEmail;
    }

    public void setAttendeeEmail(String attendeeEmail) {
        AttendeeEmail = attendeeEmail;
    }

    public String getAttendeeType() {
        return AttendeeType;
    }

    public void setAttendeeType(String attendeeType) {
        AttendeeType = attendeeType;
    }
}
