package com.kmarinov.serialize.dto;

import java.util.List;

import org.springframework.data.util.Pair;

import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.util.CentroidDensityPair;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClusterDto {
	private String forProperty;
	private Double expectedDensity;
	private List<CharachteristicsEnum> variables;
	private List<List<Double>> dots;
	private List<Double> centroid;
	private List<Pair<String, Pair<Double, Double>>> distributionFunctions;
	
}
