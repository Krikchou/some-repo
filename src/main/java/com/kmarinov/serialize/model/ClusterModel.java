package com.kmarinov.serialize.model;

import java.util.List;

import com.kmarinov.serialize.util.CentroidDensityPair;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClusterModel {
	private List<CentroidDensityPair> centroids;
	
	
}
