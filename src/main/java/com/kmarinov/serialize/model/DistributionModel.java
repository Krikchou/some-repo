package com.kmarinov.serialize.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

import lombok.Data;

@Data
public class DistributionModel {
	private String serviceRelated;
	private Pair<BigDecimal, BigDecimal> range;
	private Map<BigDecimal, Double> probablityDistribution;
	private List<BigDecimal> orderedAllUniqueValues;
	private Integer amountOfRecords;
	private BigDecimal mean;
	
	public boolean isValueInRange(BigDecimal value) {
		return range.getFirst().compareTo(value) != 1 
				&& range.getSecond().compareTo(value) != -1 ;
	}
	
	public Double approxProbabilityOfValueOccurance(BigDecimal value) {
		if(this.isValueInRange(value)) {
			Double probablity = this.probablityDistribution.get(value);
			if(probablity == null) {
				BigDecimal curr = BigDecimal.ZERO;
				for(BigDecimal decimal : orderedAllUniqueValues) {
					if (decimal.compareTo(value) == 1) {
						curr = decimal;
					} else {
						return ((probablityDistribution.get(curr) + probablityDistribution.get(decimal))/2);
					}
				}
			}
			
			return probablity;
		}
		
		return 0d;
	}
}
