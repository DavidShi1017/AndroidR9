package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class FAQCategory implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Questions")
	private List<FAQQuestion> questions = new ArrayList<FAQQuestion>();
	
	public String getTitle() {
		return title;
	}
	public List<FAQQuestion> getQuestion() {
		return questions;
	}

	
	public FAQCategory(String title, List<FAQQuestion> questions){
		this.title = title;
		this.questions = questions;
	}
}
 