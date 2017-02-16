package com.example.divgarg.calendarutil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button qry = (Button) findViewById(R.id.button);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }

        qry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new FetchCalendarEventsTask().execute();
            }
        });
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
                        Event e = new Event(eventIds[position], titles[position]);
                        new FetchEventAttendeesEmailsTask().execute(e);
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

    protected void sendEmail(Mail email) {
        Log.v("Send EMail", email.getSubject());
    }


    private class FetchCalendarEventsTask extends AsyncTask<Void , Void, List<Event>>{
        String TAG = "FetchCalendarEventsTask";
        @Override
        protected  List<Event> doInBackground(Void... params) {
            Log.v(TAG, "doInBackground");
            List<Event> eventList = CalendarService.readCalendar(getApplicationContext());
            return eventList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute( List<Event> eventList) {
            super.onPostExecute(eventList);
            Log.v(TAG, "onPostExecute");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (eventList != null && eventList.size() > 0) {
                createPopUp(eventList);
            } else {
                Log.v(TAG, "Data is null");
                return;
            }
        }

    }

    private class FetchEventAttendeesEmailsTask extends AsyncTask<Event, Void, Mail>{
        String TAG = "FetchAttendeesEmails";

        @Override
        protected Mail doInBackground(Event... params) {
            Log.v(TAG, "doInBackground");
            Event selectedEvent = params[0];
            HashSet<EventAttendee> eventAttendees = CalendarService.getEventAttendees(getApplicationContext(), selectedEvent.getEventId());
            List<String> attendeeEmail = new ArrayList<>();
            for (EventAttendee attendee : eventAttendees) {
                Log.v(TAG, attendee.getAttendeeEmail());
                attendeeEmail.add(attendee.getAttendeeEmail());
            }
            Mail mail = new Mail(selectedEvent.getTitle(), attendeeEmail);
            return mail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "onPreExecute");
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Mail mail) {
            super.onPostExecute(mail);
            Log.v(TAG, "onPostExecute");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Sending email", Toast.LENGTH_LONG).show();
            sendEmail(mail);
        }

    }

}
