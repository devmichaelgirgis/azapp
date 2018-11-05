package com.exceedgulf.alainzoo.models;


import com.google.gson.annotations.SerializedName;

public class FeedbackModel {

    @SerializedName("webform_id")
    private String webformId;

    @SerializedName("name")
    private String name;

    @SerializedName("comment")
    private String comment;

    @SerializedName("category")
    private int category;

    @SerializedName("type")
    private String type;

    @SerializedName("email")
    private String email;

    public String getWebformId() {
        return webformId;
    }

    public void setWebformId(String webformId) {
        this.webformId = webformId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return
                "FeedbackModel{" +
                        "webform_id = '" + webformId + '\'' +
                        ",name = '" + name + '\'' +
                        ",comment = '" + comment + '\'' +
                        ",category = '" + category + '\'' +
                        ",type = '" + type + '\'' +
                        ",email = '" + email + '\'' +
                        "}";
    }
}