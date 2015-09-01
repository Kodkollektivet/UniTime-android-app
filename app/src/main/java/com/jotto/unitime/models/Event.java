package com.jotto.unitime.models;

import com.orm.SugarRecord;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by otto on 2015-06-19.
 */

/**
 * Represents an event with all information availible to it.
 */
public class Event extends SugarRecord<Event> implements Comparable<Event> {
    private String startdate;
    private String starttime;
    private String endtime;
    private String info;
    private String room;
    private String teacher;
    private String course_code;
    private String name_en;
    private String name_sv;
    private String desc;

    public Event() {
    }

    /**
     * Constructor for an Event.
     * @param startdate Event's start date
     * @param starttime Event's start time
     * @param endtime Event's end time
     * @param info Event's info
     * @param room Event's classroom
     * @param teacher Event's teacher
     * @param course_code Course's course code
     * @param name_en Course's English name
     * @param name_sv Course's Swedish name
     * @param desc Event's description
     */
    public Event(String startdate, String starttime, String endtime, String info, String room, String teacher, String course_code, String name_en, String name_sv, String desc) {
        this.startdate = startdate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.info = info;
        this.room = room;
        this.teacher = teacher;
        this.course_code = course_code;
        this.name_en = name_en;
        this.name_sv = name_sv;
        this.desc = desc;
    }

    /**
     * Gets the events startdate.
     * @return Event's startdate
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     * Sets the events startdate.
     * @param startdate Event's startdate
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    /**
     * Gets the events starttime.
     * @return Event's starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * Sets the events starttime.
     * @param starttime Event's starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     * Gets the events endtime.
     * @return Event's endtime
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     * Sets the events endtime.
     * @param endtime Event's endtime
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    /**
     * Gets the events info.
     * @return Event's info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the events info.
     * @param info Event's info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Gets the events classroom.
     * @return Event's classroom
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the events classroom.
     * @param room Event's classroom
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets the events teacher.
     * @return Event's teacher
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * Sets the event's teacher.
     * @param teacher Event's teacher
     */
    public void setTeacher(String teacher) {
        this.teacher = teacher;
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
     * Gets the course's English name.
     * @return Course's English name
     */
    public String getName_en() {
        return name_en;
    }

    /**
     * Sets the course's English name.
     * @param name_en Course's English name
     */
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    /**
     * Gets the course's Swedish name.
     * @return Course's Swedish name
     */
    public String getName_sv() {
        return name_sv;
    }

    /**
     *Sets the course's Swedish name.
     * @param name_sv Course's Swedish name
     */
    public void setName_sv(String name_sv) {
        this.name_sv = name_sv;
    }

    /**
     * Gets the event's description.
     * @return Event's description
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the event's description.
     * @param desc Event's description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * CompareTo method for events. Sorts them by startdate and starttime.
     * @param event Event
     * @return CompareTo value
     */
    @Override
    public int compareTo(Event event) {
        int value = LocalDate.parse(this.startdate).compareTo(LocalDate.parse(event.startdate));
        if (value == 0) {
            DateTime current = new LocalDate(this.startdate).toDateTime(new LocalTime(this.getStarttime()));
            DateTime compare = new LocalDate(event.startdate).toDateTime(new LocalTime(event.getStarttime()));
            return current.compareTo(compare);
        }
        else {
            return value;
        }
    }
}
