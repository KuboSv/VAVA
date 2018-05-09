package com.server.entity;

import java.io.Serializable;

/**
 * @author Jakub Juško, Ivan Petrov
 */
public class Question implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	private String question;
	private String answer;
	private boolean correct;
	
	public Question() {
		
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	

}
