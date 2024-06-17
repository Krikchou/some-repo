package com.kmarinov.serialize.util;

import java.util.List;

import org.springframework.data.util.Pair;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CentroidDensityPair {
	private String forProperty;
	private Pair<Double, Double> centoroid;
	private Double expectedDensity;
	private List<Pair<Double, Double>> dots;
	private List<Pair<String, Pair<Double, Double>>> distributionFunctions;
}
