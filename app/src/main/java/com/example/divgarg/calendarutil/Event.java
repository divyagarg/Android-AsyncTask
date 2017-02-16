package com.example.divgarg.calendarutil;

/**
 * Created by divgarg on 2/10/17.
 */

public class Event {
    private Long eventId;

    private String title;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
