package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-19.
 */
public class Course extends SugarRecord<Course> implements Comparable<Course> {
    private String name;
    private String url;
    private String semester;
    private String year;
    private String course_id;
    private String course_code;

    public Course() {
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getSemester() {
        return semester;
    }

    public String getYear() {
        return year;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }


    @Override
    public int compareTo(Course course) {
        return this.course_code.compareTo(course.course_code);
    }
}

