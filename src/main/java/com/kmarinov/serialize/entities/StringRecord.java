package com.kmarinov.serialize.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("stringRecord")
public class StringRecord extends Record {
	@Column(name = "STRING_VALUE")
	private String value;
	private Boolean containsWords;
	private Integer length;
	private String pattern;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getContainsWords() {
		return containsWords;
	}

	public void setContainsWords(Boolean containsWords) {
		this.containsWords = containsWords;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
