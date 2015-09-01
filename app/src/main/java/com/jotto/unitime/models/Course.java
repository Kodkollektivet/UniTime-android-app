package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-19.
 */

/*
Course class, also used as database model
 */

/**
 * Represents a course with all availible information about it.
  */
public class Course extends SugarRecord<Course> implements Comparable<Course> {
    private String name_en;
    private String name_sv;
    private String syllabus_sv;
    private String syllabus_en;
    private String course_code;
    private String course_id;
    private String course_points;
    private String course_location;
    private String course_language;
    private String course_speed;
    private String semester;
    private String url;
    private String year;

    /**
     * Creates a new course with no information attatched to it.
     */
    public Course() {
    }

    /**
     * Gets the Swedish name of the course.
     * @return Swedish course name
     */
    public String getName_sv() {
        return name_sv;
    }

    /**
     * Sets the Swedish name of the course.
     * @param name_sv Swedish course name
     */
    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    /**
     * Gets the English name of the course.
     * @return English course name
     */
    public String getName_en() {
        return name_en;
    }

    /**
     * Sets the English name of the course.
     * @param name_en English course name
     */
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    /**
     * Gets the Swedish syllabus for the course.
     * @return Swedish syllabus URL
     */
    public String getSyllabus_sv() {
        return syllabus_sv;
    }

    /**
     * Sets the Swedish syllabus for the course.
     * @param syllabus_sv Swedish syllabus URL
     */
    public void setSyllabus_sv(String syllabus_sv) {
        this.syllabus_sv = syllabus_sv;
    }

    /**
     * Gets the English syllabus for the course.
     * @return English syllabus URL
     */
    public String getSyllabus_en() {
        return syllabus_en;
    }

    /**
     * Sets the English syllabus for the course.
     * @param syllabus_en English syllabus URL
     */
    public void setSyllabus_en(String syllabus_en) {
        this.syllabus_en = syllabus_en;
    }

    /**
     * Gets how many credits the course gives.
     * @return Credits
     */
    public String getCourse_points() {
        return course_points;
    }

    /**
     * Sets the credits of the course.
     * @param course_points Credits
     */
    public void setCourse_points(String course_points) {
        this.course_points = course_points;
    }

    /**
     * Gets the location where the course takes place.
     * @return Course's location
     */
    public String getCourse_location() {
        return course_location;
    }

    /**
     * Sets the location where the course takes place.
     * @param course_location Course's location
     */
    public void setCourse_location(String course_location) {
        this.course_location = course_location;
    }

    /**
     * Gets the language the course will be given in.
     * @return Course's language
     */
    public String getCourse_language() {
        return course_language;
    }

    /**
     * Sets the language the course will be given in.
     * @param course_language Course's language
     */
    public void setCourse_language(String course_language) {
        this.course_language = course_language;
    }

    /**
     * Gets the course's teaching speed.
     * For example 100% or 50% where 100% is equal to
     * a full working week.
     * @return Course's speed
     */
    public String getCourse_speed() {
        return course_speed;
    }

    /**
     * Sets the course's teaching speed.
     * For example 100% or 50% where 100% is equal to
     * a full working week.
     * @param course_speed Course's speed
     */
    public void setCourse_speed(String course_speed) {
        this.course_speed = course_speed;
    }

    /**
     * Gets the URL of the homepage for the course.
     * @return Homepage URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the homepage for the course.
     * @param url Homepage URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the semester the course will be given.
     * (HT/VT)
     * @return Course's semester
     */
    public String getSemester() {
        return semester;
    }

    /**
     * Sets the semester the course will be given.
     * (HT/VT)
     * @param semester Course's semester
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * Gets the year the course will be given.
     * @return Course's year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year the course will be given.
     * @param year Course's year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the course's id.
     * @return Course's id
     */
    public String getCourse_id() {
        return course_id;
    }

    /**
     * Sets the course's id.
     * @param course_id Course's id
     */
    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    /**
     * Gets the course's course code.
     * @return Course's course code
     */
    public String getCourse_code() {
        return course_code;
    }

    /**
     * Sets the course's course code.
     * @param course_code Course's course code
     */
    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    /**
     * CompareTo method for the Course Class
     * @param course Course to compare with
     * @return CompareTo value
     */
    @Override
    public int compareTo(Course course) {
        int code = this.course_code.compareTo(course.course_code);
        if (code == 0) {
            return this.getCourse_location().compareTo(course.getCourse_location());
        }
        else {
            return code;
        }
    }
}

