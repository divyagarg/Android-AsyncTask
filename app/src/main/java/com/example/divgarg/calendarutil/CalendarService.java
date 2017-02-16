package com.example.divgarg.calendarutil;

/**
 * Created by divgarg on 2/9/17.
 */


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.util.Log.v;


public class CalendarService {

    // Use to specify specific the time span
    public static List<Event> readCalendar(Context context) {
        Log.v("CalendarService", "Inside calendar");
        Long dtStart = System.currentTimeMillis() - 30 * 60 * 1000;
        Long dtEnd = System.currentTimeMillis() + 30 * 60 * 1000;

//        Long dtStart = getUTCEpochTime("2017-02-10 20:00:00 GMT+05:30");
//        Long dtEnd = getUTCEpochTime("2017-02-10 21:00:00 GMT+05:30");
        HashSet<EventInstance> eventInstances = getEventInstances(context, dtStart, dtEnd);
        List<Event> eventList = null;
        if (eventInstances.size() > 0) {
            for (EventInstance e : eventInstances) {

                Event event = getEvent(context, e.getEventId());

                if (event != null) {

                    if (eventList == null) {

                        eventList = new ArrayList<Event>();
                    }
                    eventList.add(event);
                    v("Event Title", event.getTitle());
                }
            }
        }
        return eventList;
    }

    private static HashSet<EventInstance> getEventInstances(Context context, long begin, long end) {
        final String[] INS_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,                           // 0
                CalendarContract.Instances.CALENDAR_ID,                  // 1

        };

        final int PROJECTION_EVENT_ID_INDEX = 0;
        final int PROJECTION_CAL_ID_INDEX = 1;
        Cursor cur = null;
        try {
            cur = CalendarContract.Instances.query(context.getContentResolver(), INS_PROJECTION, begin, end);
        } catch (Exception e) {
            e.getStackTrace();
        }
        HashSet<EventInstance> eventInstances = new HashSet<>();
        while (cur.moveToNext()) {
            Long eventId = cur.getLong(PROJECTION_EVENT_ID_INDEX);
            Long calId = cur.getLong(PROJECTION_CAL_ID_INDEX);
            EventInstance eventInstance = new EventInstance();
            eventInstance.setEventId(eventId);
            eventInstance.setCalId(calId);
            eventInstances.add(eventInstance);
        }
        return eventInstances;
    }

    private static Event getEvent(Context context, Long eventId) {
        Event event = null;
        Uri uri = CalendarContract.Events.CONTENT_URI;

        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,         // 0
                CalendarContract.Events.TITLE,       //1


        };
        final int PROJECTION_EVENT_ID_INDEX = 0;
        final int PROJECTION_EVENT_TITLE_INDEX = 1;


        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(eventId)};
        Cursor eventCursor = null;
        try {
            eventCursor = context.getContentResolver().query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            if (eventCursor.getCount() == 0) {
                throw new Exception("Event could not be found for given event id");
            }
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
        if (eventCursor.moveToNext()) {
            event = new Event();
            event.setEventId(eventCursor.getLong(PROJECTION_EVENT_ID_INDEX));
            event.setTitle(eventCursor.getString(PROJECTION_EVENT_TITLE_INDEX));

        }
        return event;
    }

    public static HashSet<EventAttendee> getEventAttendees(Context context, Long eventId) {
        final String[] ATTENDEE_PROJECTION = new String[]{
                CalendarContract.Attendees.ATTENDEE_EMAIL, //0
                CalendarContract.Attendees.ATTENDEE_TYPE, //1
        };
        final int PROJECTION_ATTENDEE_EMAIL_INDEX = 0;
        final int PROJECTION_ATTENDEE_TYPE_INDEX = 1;
        Cursor cursor = null;
        try {
            cursor = CalendarContract.Attendees.query(context.getContentResolver(), eventId, ATTENDEE_PROJECTION);
        } catch (Exception e) {
            e.getStackTrace();
        }
        HashSet<EventAttendee> attendes = new HashSet<>();
        while (cursor.moveToNext()) {
            EventAttendee obj = new EventAttendee();
            obj.setEventId(eventId);
            obj.setAttendeeEmail(cursor.getString(PROJECTION_ATTENDEE_EMAIL_INDEX));
            obj.setAttendeeType(cursor.getString(PROJECTION_ATTENDEE_TYPE_INDEX));
            attendes.add(obj);
        }
        return attendes;
    }


    // Returns a new instance of the calendar object
//    private static CalendarEvent loadEvent(Cursor csr) {
//        return new CalendarEvent(csr.getString(0),
//                new Date(csr.getLong(1)),
//                new Date(csr.getLong(2)),
//                !csr.getString(3).equals("0"));
//    }

    // Creates the list of calendar ids and returns it in a set
//    @SuppressLint("Recycle")
//    private static HashSet<Long> getCalenderIds(Context context) throws SecurityException{
//
//        final String[] CAL_PROJECTION = new String[]{
//                CalendarContract.Calendars._ID,                           // 0
//                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
//
//        };
//
//        final int PROJECTION_ID_INDEX = 0;
//        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
//
//
//
//
//        Cursor cur = null;
//        ContentResolver cr = context.getContentResolver();
//
//        Uri uri = CalendarContract.Calendars.CONTENT_URI;
//
//        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
//
//        String[] selectionArgs = new String[]{"divi191@gmail.com", "com.google",
//                "divi191@gmail.com"};
//
//        try
//        {
//            cur = cr.query(uri, CAL_PROJECTION, selection, selectionArgs, null);
//        }
//        catch (Exception e){
//            e.getStackTrace();
//        }
//        HashSet<Long> calendarIds = new HashSet<Long>();
//        while (cur.moveToNext()) {
//            Long calID = 0L;
//            String accountName = null;
//            // Get the field values
//            calID = cur.getLong(PROJECTION_ID_INDEX);
//            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
//            calendarIds.add(calID);
//
//        }
//        return calendarIds;
//
//    }

//    private static HashSet<Event> getEventsForCalendar(Context context, long calendarId) throws SecurityException
//    {
//
//        Uri uri = CalendarContract.Events.CONTENT_URI;
//
//        final String[] EVENT_PROJECTION = new String[]{
//                CalendarContract.Events._ID,         // 0
//                CalendarContract.Events.TITLE //1
//        };
//        final int PROJECTION_EVENT_ID_INDEX = 0;
//        final int PROJECTTION_EVENT_TITLE_INDEX = 1;
//
//        String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?) AND ("+
//                CalendarContract.Events.DTEND+ "<= ?) AND ("+
//                CalendarContract.Events.DTSTART+ ">= ?)";
//        Long dtStart = getUTCEpochTime("2017-02-09 10:00:00 GMT+05:30");
//        Long dtEnd = getUTCEpochTime("2017-02-09 11:00:00 GMT+05:30");
//        String[] selectionArgs = new String[]{String.valueOf(calendarId), String.valueOf(dtStart), String.valueOf(dtEnd)};
//        Cursor eventCursor=null;
//        try{
//            eventCursor = context.getContentResolver().query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
//        }catch (Exception e){
//            e.getStackTrace();
//        }
//
//        HashSet<Event> events = new HashSet<Event>();
//        while(eventCursor.moveToNext()){
//            Event event = new Event();
//            event.setEventId(eventCursor.getLong(PROJECTION_EVENT_ID_INDEX));
//            event.setTitle(eventCursor.getString(PROJECTTION_EVENT_TITLE_INDEX));
//            events.add(event);
//        }
//        return events;
//    }


//    private static Long getUTCEpochTime(String s) {
//        Long timeInMilliseconds = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//        try {
//            Date mDate = sdf.parse(s);
//            timeInMilliseconds = mDate.getTime();
//            d("timeInMilliseconds", " = " + timeInMilliseconds);
//
//        } catch (ParseException e) {
//            e("Error", " = " + e.getStackTrace());
//        }
//        return timeInMilliseconds;
//    }

}

//        for(EventInstance e: eventInstances){
//
//            HashSet<EventAttendee> attendees = getEventAttendees(context, event.getEventId());
//            for(EventAttendee a : attendees)
//            {
//                v("Attendees Email", a.getAttendeeEmail());
//                v("Attendee type", a.getAttendeeType());
//                Toast.makeText(context, a.getAttendeeEmail(), Toast.LENGTH_LONG).show();
//            }
//
//        }


//        // Create a set containing all of the calendar IDs available on the phone
//        HashSet<Long> calendarIds = getCalenderIds(context);
//
//
//        // Loop over all of the calendars
//        for (Long id : calendarIds)
//        {
//            HashSet<Event> events = getEventsForCalendar(context, id);
//            for(Event event: events)
//            {
//                Toast.makeText(context, event.getDescription(), Toast.LENGTH_LONG).show();
//            }
//        }