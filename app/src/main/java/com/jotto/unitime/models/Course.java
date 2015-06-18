package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-19.
 */
public class Course extends SugarRecord<Course> {
    private String season;
    private String course_code;
    private String year;
    private String course_anmalningskod;
    private String html_url;
    private String model;
    private String pk;

    public Course() {
    }

    public Course(String season, String course_code, String year, String course_anmalningskod,
                  String html_url, String model, String pk) {
        this.season = season;
        this.course_code = course_code;
        this.year = year;
        this.course_anmalningskod = course_anmalningskod;
        this.html_url = html_url;
        this.model = model;
        this.pk = pk;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCourse_anmalningskod() {
        return course_anmalningskod;
    }

    public void setCourse_anmalningskod(String course_anmalningskod) {
        this.course_anmalningskod = course_anmalningskod;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
}
