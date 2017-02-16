package com.example.divgarg.calendarutil;

import java.util.List;

/**
 * Created by divgarg on 2/16/17.
 */

public class Mail {

    String subject;
    List<String> emailIds;

    public Mail(String subject, List<String> emailIds) {
        this.subject = subject;
        this.emailIds = emailIds;
    }

    public String getSubject() {
        return subject;
    }



    public List<String> getEmailIds() {
        return emailIds;
    }


}
