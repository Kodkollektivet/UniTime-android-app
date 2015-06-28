package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-28.
 */
public class CourseDataAC extends SugarRecord<CourseDataAC>{
    private String course_code;
    private String name_sv;
    private String name_en;

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
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
}
