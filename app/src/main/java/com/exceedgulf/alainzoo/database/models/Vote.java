package com.exceedgulf.alainzoo.database.models;


import java.io.Serializable;

/**
 * Created by Paras Ghasadiya on 02/01/18.
 */

public class Vote implements Serializable {

    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
