package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

public class SearchDataModel{

	@SerializedName("banner")
	private String banner;

	@SerializedName("details")
	private String details;

	@SerializedName("id")
	private int id;

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private String title;

	public void setBanner(String banner){
		this.banner = banner;
	}

	public String getBanner(){
		return banner;
	}

	public void setDetails(String details){
		this.details = details;
	}

	public String getDetails(){
		return details;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}