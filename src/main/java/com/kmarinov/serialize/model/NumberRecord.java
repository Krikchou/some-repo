package com.kmarinov.serialize.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "NUMBER_RECORD")
public class NumberRecord extends Record {
	private BigDecimal value;
	private Boolean wholeNumber;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Boolean getWholeNumber() {
		return wholeNumber;
	}

	public void setWholeNumber(Boolean wholeNumber) {
		this.wholeNumber = wholeNumber;
	}

}
