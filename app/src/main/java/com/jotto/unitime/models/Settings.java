package com.jotto.unitime.models;

import com.orm.SugarRecord;

import org.joda.time.LocalDate;

/**
 * Created by otto on 2015-06-28.
 */

/**
 * Model and class used to save settings to the database
 */
public class Settings extends SugarRecord<Settings> {
    private int contentLength;
    private String date;

    /**
     * Initializes a new empty settings instance.
     */
    public Settings() {

    }

    /**
     * Constructor for Settings class
     * @param contentLength HTTP Content Length
     * @param date Last date the settings were checked
     */
    public Settings(int contentLength, String date) {
        this.contentLength = contentLength;
        this.date = date;
    }

    /**
     * Gets the date the settings where last checked.
     * @return LocalDate
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date the settings where last checked.
     * @param date LocalDate
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the content length from settings.
     * @return HTTP Content Length
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Sets the content length in settings.
     * @param contentLength HTTP Content Length
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
