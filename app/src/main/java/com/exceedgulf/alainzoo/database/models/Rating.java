package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rating{

	@SerializedName("rating")
	private List<RatingItem> rating;

	public void setRating(List<RatingItem> rating){
		this.rating = rating;
	}

	public List<RatingItem> getRating(){
		return rating;
	}
}