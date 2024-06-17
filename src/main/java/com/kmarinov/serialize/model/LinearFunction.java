package com.kmarinov.serialize.model;

import org.springframework.data.util.Pair;

import lombok.AllArgsConstructor;

public class LinearFunction implements Function {

	private Pair<Double, Double> pointA;

	private Pair<Double, Double> pointB;
	
	private Double indx;
	
	private Double secondParam;
	
	public LinearFunction(Pair<Double, Double> pointA, Pair<Double, Double> pointB) {
		this.pointA = pointA;
		this.pointB = pointB;
		
		this.indx = (pointA.getSecond() - pointB.getSecond()) / ((pointA.getFirst() - pointB.getFirst()));
		this.secondParam = pointA.getSecond() - pointA.getFirst() * indx;
	}

	@Override
	public Double compute(Double d) {
		return indx * d + secondParam;
	}

	@Override
	public String render() {
		return indx.toString() + "*x+" + secondParam.toString();
	}

	@Override
	public Pair<Double, Double> getRange() {
		return Pair.of(this.pointA.getFirst(), this.pointB.getFirst());
	}
}
