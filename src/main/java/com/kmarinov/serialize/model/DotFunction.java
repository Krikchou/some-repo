package com.kmarinov.serialize.model;

import org.springframework.data.util.Pair;

public class DotFunction implements Function {
	
	private Double records;
	
	public DotFunction(Double records) {
		this.records = records;
	}

	@Override
	public Double compute(Double atValue) {
		if (atValue == 0) {
			return records;
		} else {
			return 0d;
		}
	}

	@Override
	public String render() {
		return "1*x+" + records;
	}

	@Override
	public Pair<Double, Double> getRange() {
		return Pair.of(0d,  0d);
	}

}
