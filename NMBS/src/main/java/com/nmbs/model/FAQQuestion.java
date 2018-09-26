package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class FAQQuestion implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Question")
	private String question;
	
	@SerializedName("Answer")
	private String answer;

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}
	
	public FAQQuestion(String question, String answer){
		this.question = question;
		this.answer = answer;
	}
}
