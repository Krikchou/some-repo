package com.kmarinov.serialize.util;

import java.util.List;

import org.springframework.data.util.Pair;

import com.kmarinov.serialize.enums.CharachteristicsEnum;

public class ClusterUtil {
	
	private static final char ALPHABETIC = 'A';
	private static final char DIGIT = 'D';

	public static CentroidDensityPair nearestCentroid(Pair<Double, Double> rec,
			List<CentroidDensityPair> centroids) {
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
			return computePatternEncoding(r.matches("[^a-zA-Z0-9]+") || r.contains(" ") ? null : generateStringPattern(r));
		case WORD_COUNT:
			return wordCount(r);
		case MEAN_STRING_VALUE:
			return computeMeanStringValue(r);

		}
		return Double.MIN_NORMAL;
	}
	
	private static Double computePatternEncoding(String pattern) {
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

	private static Double wordCount(String val) {
		return Double.valueOf(val.split("\\s+").length);
	}

	private static Double computeMeanStringValue(String val) {
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
}
