package com.kmarinov.serialize.model;

import org.springframework.data.util.Pair;

public interface Function {
	Double compute(Double atValue);
	
	String render();
	
	Pair<Double, Double> getRange();
}
