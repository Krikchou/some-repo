package com.kmarinov.serialize.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.util.Pair;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder 
@ToString
public class Cluster {
	private String name;
	private Double[] coords;
	private List<Double[]> points;
	private Double meanDistr;
	private Function<Double, Double> distributionGradient;
	
	public void addPoint(Triple<String, Double, Double> pt) {
		if (Objects.isNull(points)) {
			points = new ArrayList<>();
		}
		
		points.add(new Double[] {pt.getMiddle(), pt.getRight()});
	}
	
	public void addPoint(Pair<String, List<Double>> pt) {
		if (Objects.isNull(points)) {
			points = new ArrayList<>();
		}
		
		points.add(pt.getSecond().toArray(new Double[0]));
	}
	
	@Override
	public Cluster clone() {
		return new Cluster(name, coords, points, meanDistr, distributionGradient);
	}
}
