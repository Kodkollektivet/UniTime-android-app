package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-28.
 */
/*
CourseDataAC class, also used as database model for the courselist
 */

/**
 * Represents a course with less information, to be used when gathered in a large list.
  */
public class CourseDataAC extends SugarRecord<CourseDataAC>{
    private String course_code;
    private String name_sv;
    private String name_en;
    private String location;

    /**
     * Gets the course code for the specific course.
     * @return Course code
     */
    public String getCourse_code() {
        return course_code;
    }

    /**
     * Sets the course code for the specific course.
     * @param course_code The course code to be associated with the course
     */
    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    /**
     * Gets the Swedish name of the course.
     * @return Course's name in Swedish
     */
    public String getName_sv() {
        return name_sv;
    }

    /**
     * Sets the Swedish name of the course.
     * @param name_sv Course's name in Swedish
     */
    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    /**
     * Gets the English name of the course.
     * @return Course's name in English
     */
    public String getName_en() {
        return name_en;
    }

    /**
     * Sets the English name of the course.
     * @param name_en Course's name in English
     */
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    /**
     * Gets the location where the course takes place.
     * @return Course's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location where the course takes place.
     * @param location Course's location
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
