package com.jotto.unitime.models;

import com.orm.SugarRecord;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by otto on 2015-06-19.
 */
public class Event extends SugarRecord<Event> implements Comparable<Event> {
    private String startdate;
    private String starttime;
    private String endtime;
    private String info;
    private String room;
    private String teacher;
    private String course_code;
    private String course_name;
    private String desc;

    public Event() {
    }

    public Event(String startdate, String starttime, String endtime, String info, String room,
                 String teacher, String course_code, String course_name, String desc) {
        this.startdate = startdate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.info = info;
        this.room = room;
        this.teacher = teacher;
        this.course_code = course_code;
        this.course_name = course_name;
        this.desc = desc;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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
