package com.example.divgarg.calendarutil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CALENDAR_EVENT_LOADER = 22;
    private static final int CALENDAR_EVENT_ATTENDEES_LOADER = 23;
    private ProgressBar mLoadingIndicator;
    LoaderManager loaderManager;

    private LoaderManager.LoaderCallbacks<List<Event>> eventsLoaderListener = new LoaderManager.LoaderCallbacks<List<Event>>() {

        @Override
        public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
            Log.v("eventsLoaderListener", "In Create Loader");
            return new AsyncTaskLoader<List<Event>>(getApplicationContext()) {

                @Override
                protected void onStartLoading() {
                    Log.v("eventsLoaderListener", "On start loading");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                @Override
                public List<Event> loadInBackground() {
                    Log.v("eventsLoaderListener", "Loading in background");
                    List<Event> eventList = CalendarService.readCalendar(getApplicationContext());
                    return eventList;
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
            Log.v("eventsLoaderListener", "On Load finished");
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (data != null && data.size() > 0) {
                createPopUp(data);
                //stopLoader(CALENDAR_EVENT_LOADER);
            } else {
                Log.v("eventsLoaderListener", "Data is null");
                return;
            }
            return;
        }

        @Override
        public void onLoaderReset(Loader<List<Event>> loader) {
            Log.v("eventsLoaderListener", "On Load Reset");
        }
    };


    private LoaderManager.LoaderCallbacks<Mail> eventAttendeesLoaderListener = new LoaderManager.LoaderCallbacks<Mail>() {
        @Override
        public Loader<Mail> onCreateLoader(int id, final Bundle args) {
            Log.v("eventAttendees", "Inside onCreateLoader");
            return new AsyncTaskLoader<Mail>(getApplicationContext()) {


                @Override
                protected void onStartLoading() {
                    Log.v("eventAttendees", "On start loading");

                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                @Override
                public Mail loadInBackground() {
                    Log.v("eventAttendees", "Loading Attendees in background");
                    Long eventId = args.getLong("eventId");
                    String title = args.getString("title");
                    HashSet<EventAttendee> eventAttendees = CalendarService.getEventAttendees(getApplicationContext(), eventId);
                    List<String> attendeeEmail = new ArrayList<>();
                    for (EventAttendee attendee : eventAttendees) {
                        Log.v("ATTENDEE EMAIL", attendee.getAttendeeEmail());
                        attendeeEmail.add(attendee.getAttendeeEmail());
                    }
                    Mail mail = new Mail(title, attendeeEmail);
                    //Toast.makeText(getApplicationContext(), "Email will be sent", Toast.LENGTH_SHORT).show();
                    return mail;
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<Mail> loader, Mail data) {
            Log.v("eventAttendees", "Inside onLoadFinished");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            sendEmail(data);
        }

        @Override
        public void onLoaderReset(Loader<Mail> loader) {
            Log.v("eventAttendees", "OnLoaderReset");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button qry = (Button) findViewById(R.id.button);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
//        getSupportLoaderManager().initLoader(CALENDAR_EVENT_LOADER, null, eventsLoaderListener);
//        getSupportLoaderManager().initLoader(CALENDAR_EVENT_ATTENDEES_LOADER, null, eventAttendeesLoaderListener);

        qry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadEventLoader();
            }
        });
    }

    private void loadEventLoader() {
        loaderManager = getSupportLoaderManager();
        Loader<List<Event>> loader = loaderManager.getLoader(CALENDAR_EVENT_LOADER);
        if (loader == null) {
            loaderManager.initLoader(CALENDAR_EVENT_LOADER, null, eventsLoaderListener);
        } else {
            loaderManager.restartLoader(CALENDAR_EVENT_LOADER, null, eventsLoaderListener);
        }

    }

    private void loadEventAttendeeLoader(Long eventId, String title) {

        loaderManager = getSupportLoaderManager();
        Loader<Mail> loader = loaderManager.getLoader(CALENDAR_EVENT_ATTENDEES_LOADER);
        Bundle args = new Bundle(2);
        args.putLong("eventId", eventId);
        args.putString("title", title);
        if (loader == null) {
            loaderManager.initLoader(CALENDAR_EVENT_ATTENDEES_LOADER, args, eventAttendeesLoaderListener);
        } else {
            loaderManager.restartLoader(CALENDAR_EVENT_ATTENDEES_LOADER, args, eventAttendeesLoaderListener);
        }
    }


    private void createPopUp(final List<Event> events) {
        Log.v("eventsLoader", "Inside Create Popup");
        List<Long> eventIdList = new ArrayList<>();
        List<String> eventTitleList = new ArrayList<>();
        for (Event e : events) {
            eventIdList.add(e.getEventId());
            eventTitleList.add(e.getTitle());
        }
        final Long[] eventIds = eventIdList.toArray(new Long[eventIdList.size()]);
        final String[] titles = eventTitleList.toArray(new String[eventTitleList.size()]);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to email file to all attendees of following events")
                .setSingleChoiceItems(titles, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Toast.makeText(getApplicationContext(), titles[position] + "Event Id: " + eventIds[position], Toast.LENGTH_SHORT).show();
                        loadEventAttendeeLoader(eventIds[position], titles[position]);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void sendEmail(Mail email) {
        Log.v("Send EMail", email.getSubject());
    }


}
