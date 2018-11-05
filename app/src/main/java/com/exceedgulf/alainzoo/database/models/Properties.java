package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;


public class Properties{

	@SerializedName("area")
	private String area;

	@SerializedName("path")
	private String path;

	@SerializedName("thumbnail")
	private String thumbnail;

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("id")
	private String id;

	@SerializedName("category")
	private String category;

	@SerializedName("type")
	private String type;

	@SerializedName("body")
	private String body;

	public void setArea(String area){
		this.area = area;
	}

	public String getArea(){
		return area;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return path;
	}

	public void setThumbnail(String thumbnail){
		this.thumbnail = thumbnail;
	}

	public String getThumbnail(){
		return thumbnail;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return icon;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setBody(String body){
		this.body = body;
	}

	public String getBody(){
		return body;
	}
}