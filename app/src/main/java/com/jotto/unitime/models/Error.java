package com.jotto.unitime.models;

import com.orm.SugarRecord;

/**
 * Created by otto on 2015-06-24.
 */
public class Error {

    String message;

    public Error() {}

    public Error(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
