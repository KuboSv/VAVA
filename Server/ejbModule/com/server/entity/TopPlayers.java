package com.server.entity;

import java.io.Serializable;

/**
 * @author Jakub Juško, Ivan Petrov
 */
public class TopPlayers implements Serializable {
	
	private String Name;
	private int score;
	
	public TopPlayers() {
	
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}