package com.exceedgulf.alainzoo.models;

import java.util.ArrayList;

/**
 * Created by R.S. on 21/12/17
 */
public class FAQModel {

    private int id;
    private String name;
    private ArrayList<FaqQuestionModel> questionModelArrayList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FaqQuestionModel> getQuestionModelArrayList() {
        return questionModelArrayList;
    }

    public void setQuestionModelArrayList(ArrayList<FaqQuestionModel> questionModelArrayList) {
        this.questionModelArrayList = questionModelArrayList;
    }
}
