package com.kmarinov.serialize.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("numberRecord")
public class NumberRecord extends Record {
	@Column(name = "NUMERIC_VALUE")
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
