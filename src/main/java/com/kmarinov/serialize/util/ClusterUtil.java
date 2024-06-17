package com.kmarinov.serialize.util;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.util.Pair;

import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.Cluster;
import com.kmarinov.serialize.entities.NumberRecord;
import com.kmarinov.serialize.entities.ObjectRecord;
import com.kmarinov.serialize.entities.Record;
import com.kmarinov.serialize.entities.StringRecord;

public class ClusterUtil {

	private static final char ALPHABETIC = 'A';
	private static final char DIGIT = 'D';

	public static CentroidDensityPair nearestCentroid(Pair<Double, Double> rec, List<CentroidDensityPair> centroids) {
		double minimumDistance = Double.MAX_VALUE;
		CentroidDensityPair nearest = null;
		for (CentroidDensityPair centroid : centroids) {
			double currentDistance = ClusterUtil.calculateDistance(centroid.getCentoroid(), rec);
			if (currentDistance < minimumDistance) {
				minimumDistance = currentDistance;
				nearest = centroid;
			}
		}

		return nearest;
	}

	private static Double calculateDistance(Pair<Double, Double> ptA, Pair<Double, Double> ptB) {
		return Math.sqrt(Math.pow(ptA.getFirst() - ptB.getFirst(), 2) + Math.pow(ptA.getSecond() - ptB.getSecond(), 2));
	}

	public static Double determineValue(String r, CharachteristicsEnum e) {
		switch (e) {
		case SIZE:
			return Double.valueOf(r.length());
		case PATTERN:
			return computePatternEncoding(
					r.matches("[^a-zA-Z0-9]+") || r.contains(" ") ? null : generateStringPattern(r));
		case WORD_COUNT:
			return wordCount(r);
		case MEAN_STRING_VALUE:
			return computeMeanStringValue(r);

		}
		return Double.MIN_NORMAL;
	}

	public static Double determineValue(Record r, CharachteristicsEnum e) {
		if (r instanceof StringRecord) {
			switch (e) {
			case SIZE:
				return Double.valueOf(((StringRecord) r).getValue().length());
			case PATTERN:
				return computePatternEncoding(((StringRecord) r).getValue().matches("[^a-zA-Z0-9]+")
						|| ((StringRecord) r).getValue().contains(" ") ? null
								: generateStringPattern(((StringRecord) r).getValue()));
			case WORD_COUNT:
				return wordCount(((StringRecord) r).getValue());
			case MEAN_STRING_VALUE:
				return computeMeanStringValue(((StringRecord) r).getValue());
			default:
				return Double.MIN_NORMAL;
			}
		} else if (r instanceof NumberRecord) {
			switch (e) {
			case WHOLE_NUMBER:
				return ((NumberRecord) r).getValue().divideAndRemainder(BigDecimal.ONE)[1].doubleValue();
			case NUMERIC_VALUE:
				return ((NumberRecord) r).getValue().doubleValue();
			default:
				return Double.MIN_NORMAL;
			}

		} else if (r instanceof ObjectRecord) {
			ObjectRecord obj = (ObjectRecord) r;
			if (obj.getIsArray()) {
				
			}
		}
		
		return Double.MIN_NORMAL;
	}

	public static Double computePatternEncoding(String pattern) {
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

	public static Double wordCount(String val) {
		return Double.valueOf(val.split("\\s+").length);
	}

	public static Double computeMeanStringValue(String val) {
		Double res = 0d;
		for (char c : val.toCharArray()) {
			res += c;
		}

		return res / val.length();
	}

	public static String generateStringPattern(String string) {
		StringBuilder builder = new StringBuilder();
		for (char c : string.toCharArray()) {
			if (Character.isLetter(c)) {
				builder.append(ALPHABETIC);
			} else {
				builder.append(DIGIT);
			}
		}
		return builder.toString();
	}

	public static Double calculateDistance(Cluster ptA, Pair<String, List<Double>> ptB) {
		return calculateDistance(List.of(ptA.getCoords()), ptB.getSecond());
	}

	public static Double calculateDistance(List<Double> ptA, List<Double> ptB) {
		Double sum = 0d;
		for (int i = 0; i < ptA.size(); i++) {
			sum += Math.pow(ptA.get(i) - ptB.get(i), 2);
		}

		return Math.sqrt(sum);
	}

	// TODO lfind furthest point from centroid
	public static Pair<Double, Double> findFurthest() {
		return Pair.of(null, null);
	}

	public static Double computeDensityOfCluster() {
		return 0d;
	}
}
