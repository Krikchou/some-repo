package com.kmarinov.serialize.alogithm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.config.ConfigProperties;
import com.kmarinov.serialize.dao.NumberRecordRepository;
import com.kmarinov.serialize.dao.StringRecordRepository;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.DistributionModel;
import com.kmarinov.serialize.model.NumberRecord;
import com.kmarinov.serialize.model.StringRecord;
import com.kmarinov.serialize.util.CentroidDensityPair;

@Service
public class ModelServiceImpl implements ModelService {
	@Autowired
	private ConfigProperties props;

	@Autowired
	private NumberRecordRepository numberDao;

	@Autowired
	private StringRecordRepository stringDao;

	@Override
	public Map<String, DistributionModel> generateDistributionModelsForService(String service) {
		List<String> recordNames = numberDao.findDistinctRecordsForService(service);

		return recordNames.parallelStream()
				.collect(Collectors.toMap((String e) -> e, (String e) -> generateDistributionModel(service, e)));
	}

	private DistributionModel generateDistributionModel(String service, String recordName) {
		List<NumberRecord> records = numberDao.findByServiceRelatedAndRecordNameOrderByValueAsc(service, recordName);
		DistributionModel model = new DistributionModel();
		model.setRange(Pair.of(records.get(0).getValue(), records.get(records.size() - 1).getValue()));
		model.setAmountOfRecords(records.size());
		model.setMean(records.stream().map(e -> e.getValue()).reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(records.size()), MathContext.DECIMAL64));
		model.setServiceRelated(service);
		model.setOrderedAllUniqueValues(numberDao.listDistinctValues(service, recordName));
		Map<BigDecimal, Double> probabilityDistribution = new HashMap<>();
		BigDecimal currentValue = records.get(0).getValue();
		double accumulator = 0;
		for (BigDecimal value : records.stream().map(e -> e.getValue()).collect(Collectors.toList())) {
			if (value.compareTo(currentValue) == 0) {
				accumulator++;
			} else {
				probabilityDistribution.put(currentValue, accumulator / records.size());
				accumulator = 1;
				currentValue = value;
			}
		}

		model.setProbablityDistribution(probabilityDistribution);

		return model;
	}

	@Override
	public Map<String, Map<String, DistributionModel>> generateDistributionModelsForServices() {
		return props.getConfigurationEnabledFor().parallelStream()
				.collect(Collectors.toMap((String e) -> e, (String e) -> generateDistributionModelsForService(e)));
	}

	@Override
	public ClusterModel generateClusterModelForServiceOnCharacteristicPairs(String s, CharachteristicsEnum char1,
			CharachteristicsEnum char2) {
		List<String> recordNames = stringDao.findDistinctRecordsForService(s);
		List<Triple<String, Double, Double>> charList = this.determineCharacteristicsList(s, char1, char2);
		List<Triple<String, Double, Double>> centroids = recordNames.stream()
		.<Triple<String, Double, Double>>mapMulti((e, c) -> {
			for(int i=0;i<props.getCentroidsPerClass();i++) {
				c.accept(initalCentroidFor(e, charList));
			}
		}).collect(Collectors.toList());
		List<Triple<String, Double, Double>> centroidHistory = new ArrayList<>();
		
		Map<Triple<String, Double, Double>, List<Triple<String, Double, Double>>> clusters = new HashMap<>();
		Map<Triple<String, Double, Double>, List<Triple<String, Double, Double>>> lastState = new HashMap<>();

	    for (int i = 0; i < props.getMaxIterations(); i++) {
	        boolean isLastIteration = i == props.getMaxIterations() - 1;

	        for (Triple<String, Double, Double> rec : charList) {
	            Triple<String, Double, Double> centroid = nearestCentroid(rec, centroids.stream()
	            		.filter(e -> e.getLeft().equals(rec.getLeft())).collect(Collectors.toList()));
	            assignToCluster(clusters, rec, centroid);
	        }

	        boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
	        lastState = clusters;
	        if (shouldTerminate) { 
	        	System.out.println(centroidHistory);
	            break; 
	        }

	        centroidHistory.addAll(centroids);
	        centroids = relocateCentroids(clusters);
	        clusters = new HashMap<>();
	    }

	    ClusterModel model = new ClusterModel();
	    model.setCentroids(centroids.stream().map(e -> {
	    	CentroidDensityPair pair = new CentroidDensityPair();
	    	pair.setCentoroid(Pair.of(e.getMiddle(), e.getRight()));
	    	pair.setForProperty(e.getLeft());
	    	
	    	return pair;
	    }).collect(Collectors.toList()));
	    
	    System.out.println(model);
	    
		return model;
	}

	private List<Triple<String, Double, Double>> determineCharacteristicsList(String s, CharachteristicsEnum char1,
			CharachteristicsEnum char2) {
		List<StringRecord> records = stringDao.findByServiceRelatedOrderByValueAsc(s);
		return records.stream().map(e -> {
			return Triple.of(e.getRecordName(), determineValue(e, char1), determineValue(e, char2));
		}).collect(Collectors.toList());
	}

	private Double determineValue(StringRecord r, CharachteristicsEnum e) {
		switch (e) {
		case SIZE:
			return Double.valueOf(r.getLength());
		case PATTERN:
			return computePatternEncoding(r.getPattern());
		case WORD_COUNT:
			return wordCount(r.getValue());
		case MEAN_STRING_VALUE:
			return computeMeanStringValue(r.getValue());

		}
		return Double.MIN_NORMAL;
	}

	private Double computePatternEncoding(String pattern) {
		if (pattern != null && pattern.length() != 0) {
			Double d = 0d;
			for (int i = 0; i < pattern.length(); i++) {
				if (pattern.charAt(i) == 'A')
					d += Math.pow(2, i);
			}

			return d;
		}

		return -1d;
	}

	private Double wordCount(String val) {
		return Double.valueOf(val.split("\\s+").length);
	}

	private Double computeMeanStringValue(String val) {
		Double res = 0d;
		for (char c : val.toCharArray()) {
			res += c;
		}

		return res / val.length();
	}

	private Triple<String, Double, Double> initalCentroidFor(String s, List<Triple<String, Double, Double>> vals) {
		Double pos1 = 0d;
		Double pos2 = 0d;
		int accum = 0;
		for (Triple<String, Double, Double> val : vals.stream().filter(e -> e.getLeft().contentEquals(s))
				.collect(Collectors.toList())) {
			pos1 += val.getMiddle();
			pos2 += val.getRight();
			++accum;
		}

		return Triple.of(s, (pos1 / accum) + Math.random()*10, (pos2 / accum) + Math.random()*10);
	}

	private void assignToCluster(Map<Triple<String, Double, Double>, List<Triple<String, Double, Double>>> centroids,
			Triple<String, Double, Double> rec, Triple<String, Double, Double> cluster) {
		centroids.compute(cluster, (key, list) -> {
			if (list == null) {
	            list = new ArrayList<>();
	        }

	        list.add(rec);
	        return list;
		});
	}

	private Triple<String, Double, Double> nearestCentroid(Triple<String, Double, Double> rec,
			List<Triple<String, Double, Double>> centroids) {
		double minimumDistance = Double.MAX_VALUE;
		Triple<String, Double, Double> nearest = null;
		for (Triple<String, Double, Double> centroid : centroids) {
			double currentDistance = calculateDistance(centroid, rec);
			if (currentDistance < minimumDistance) {
				minimumDistance = currentDistance;
				nearest = centroid;
			}
		}

		return nearest;
	}

	private Double calculateDistance(Triple<String, Double, Double> ptA, Triple<String, Double, Double> ptB) {
		return Math.sqrt(Math.pow(ptA.getMiddle() - ptB.getMiddle(), 2) + Math.pow(ptA.getRight() - ptB.getRight(), 2));
	}
	
	private List<Triple<String, Double, Double>> relocateCentroids(Map<Triple<String, Double, Double>, List<Triple<String, Double, Double>>> clusters) {
		return clusters.entrySet().stream().map(e -> average(e.getKey(), e.getValue())).collect(Collectors.toList());
	}
	
	private Triple<String, Double, Double> average(Triple<String, Double, Double> centroid, List<Triple<String, Double, Double>> records) {
		if (records == null || records.isEmpty()) { 
	        return centroid;
	    }

	    Double suml = 0d;
	    Double sumr = 0d;
	    for(Triple<String, Double, Double> rec : records) {
	    	suml += rec.getMiddle();
	    	sumr += rec.getRight();
	    }
	    
	    return Triple.of(centroid.getLeft(), suml/records.size(), sumr/records.size());
	}
}
