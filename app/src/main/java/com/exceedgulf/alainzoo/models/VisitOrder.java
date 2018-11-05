package com.exceedgulf.alainzoo.models;

import java.io.Serializable;

/**
 * Created by indianic on 28/02/18.
 */

public class VisitOrder implements Serializable {
    private String type;
    private int id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
