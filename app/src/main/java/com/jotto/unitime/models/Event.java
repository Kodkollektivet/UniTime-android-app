package com.jotto.unitime.models;

import com.orm.SugarRecord;

import org.joda.time.LocalDate;

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


    public Event() {
    }

    public Event(String startdate, String starttime, String endtime, String info, String room, String teacher) {
        this.startdate = startdate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.info = info;
        this.room = room;
        this.teacher = teacher;
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
    @Override
    public int compareTo(Event event) {
        return LocalDate.parse(this.startdate).compareTo(LocalDate.parse(event.startdate));
    }
}
