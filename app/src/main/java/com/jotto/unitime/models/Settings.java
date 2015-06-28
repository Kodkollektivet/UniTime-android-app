package com.jotto.unitime.models;

import com.orm.SugarRecord;

import org.joda.time.LocalDate;

/**
 * Created by otto on 2015-06-28.
 */
public class Settings extends SugarRecord<Settings> {
    private int contentLength;
    private String date;

    public Settings() {

    }

    public Settings(int contentLength, String date) {
        this.contentLength = contentLength;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
