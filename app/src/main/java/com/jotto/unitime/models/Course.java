package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-19.
 */
public class Course extends SugarRecord<Course> implements Comparable<Course> {
    private String name_en;
    private String name_sv;
    private String syllabus_sv;
    private String syllabus_en;
    private String course_code;
    private String course_id;
    private String course_reg;
    private String course_points;
    private String course_location;
    private String course_language;
    private String course_speed;
    private String semester;
    private String url;
    private String year;

    public Course() {
    }

    public String getName_sv() {
        return name_sv;
    }

    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getCourse_reg() {
        return course_reg;
    }

    public void setCourse_reg(String course_reg) {
        this.course_reg = course_reg;
    }

    public String getSyllabus_sv() {
        return syllabus_sv;
    }

    public void setSyllabus_sv(String syllabus_sv) {
        this.syllabus_sv = syllabus_sv;
    }

    public String getSyllabus_en() {
        return syllabus_en;
    }

    public void setSyllabus_en(String syllabus_en) {
        this.syllabus_en = syllabus_en;
    }

    public String getCourse_points() {
        return course_points;
    }

    public void setCourse_points(String course_points) {
        this.course_points = course_points;
    }

    public String getCourse_location() {
        return course_location;
    }

    public void setCourse_location(String course_location) {
        this.course_location = course_location;
    }

    public String getCourse_language() {
        return course_language;
    }

    public void setCourse_language(String course_language) {
        this.course_language = course_language;
    }

    public String getCourse_speed() {
        return course_speed;
    }

    public void setCourse_speed(String course_speed) {
        this.course_speed = course_speed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    @Override
    public int compareTo(Course course) {
        return this.course_code.compareTo(course.course_code);
    }
}

