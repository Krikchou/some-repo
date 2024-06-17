package com.kmarinov.serialize.alogithm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.config.ConfigProperties;
import com.kmarinov.serialize.dao.NumberRecordRepository;
import com.kmarinov.serialize.dao.RecordRepository;
import com.kmarinov.serialize.dao.StringRecordRepository;
import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.entities.NumberRecord;
import com.kmarinov.serialize.entities.StringRecord;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.Cluster;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.DistributionModel;
import com.kmarinov.serialize.model.DotFunction;
import com.kmarinov.serialize.model.Function;
import com.kmarinov.serialize.model.LinearFunction;
import com.kmarinov.serialize.util.CentroidDensityPair;
import com.kmarinov.serialize.util.ClusterUtil;

import com.kmarinov.serialize.entities.Record;

@Service
public class ModelServiceImpl implements ModelService {
	@Autowired
	private ConfigProperties props;

	@Autowired
	private NumberRecordRepository numberDao;

	@Autowired
	private StringRecordRepository stringDao;

	@Autowired
	private RecordRepository recordDao;

	private static final Logger LOG = LoggerFactory.getLogger(ModelService.class);

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
	public List<ClusterDto> generateClusterModelForServiceOnCharacteristics(String s, CharachteristicsEnum... chars) {
		List<String> recordNames = recordDao.findDistinctRecordsForService(s);
		List<Pair<String, List<Double>>> charList = this.determineCharacteristicsList(s, chars);
		List<Cluster> centroids = recordNames.stream().<Cluster>mapMulti((e, c) -> {
			for (int i = 0; i < props.getCentroidsPerClass(); i++) {
				c.accept(initalCentroidFor(e, charList, chars.length));
			}
		}).collect(Collectors.toList());
		List<Cluster> centroidHistory = new ArrayList<>();

		List<Cluster> lastState = new ArrayList<>();

		LOG.info("Max itr {}", props.getMaxIterations());
		for (int i = 0; i < props.getMaxIterations(); i++) {
			boolean isLastIteration = i == props.getMaxIterations() - 1;

			for (Pair<String, List<Double>> rec : charList) {
				Cluster centroid = nearestCentroid(rec, centroids.stream()
						.filter(e -> e.getName().equals(rec.getFirst())).collect(Collectors.toList()));
				centroid.addPoint(rec);

				LOG.info("Centr : {}", centroid);
			}

			boolean shouldTerminate = isLastIteration || centroids.equals(lastState);
			lastState = centroids;
			if (shouldTerminate) {
				LOG.info("History : " + centroidHistory.toString());
				break;
			}

			centroidHistory.addAll(centroids);
			centroids = relocateCentroids(centroids);
		}

		return centroids.stream().filter(e -> e.getPoints() != null).map(e -> {
			ClusterDto pair = new ClusterDto();
			pair.setCentroid(List.of(e.getCoords()));
			pair.setForProperty(e.getName());
			pair.setExpectedDensity(this.calculateDensity(e));
			pair.setDistributionFunctions(
					distributionFunction(e, 0.1d).stream().map(e1 -> Pair.of(e1.render(), e1.getRange())).toList());
			pair.setDots(e.getPoints().stream().map(e1 -> List.of(e1)).toList());
			pair.setVariables(List.of(chars));

			return pair;
		}).collect(Collectors.toList());
	}

	/**
	 * calculate dots per square metric unit of area
	 * 
	 * 
	 */
	private Double calculateDensity(Cluster c) {
		return c.getPoints().size() / (Math.PI) * Math.pow(findClusterMaxRadius(c), 2);
	}

	/**
	 * Creates multiple segregated Linear DistributionFunction from center to max
	 * radius according to step
	 * 
	 * @param percentile of radius
	 */
	private List<Function> distributionFunction(Cluster c, Double step) {
		Double rad = findClusterMaxRadius(c);
		List<Function> ret = new ArrayList<>();

		if (rad != 0) {
			int n = (int) Math.floor(1 / step);

			LOG.info("cluster name : {}", c.getName());
			LOG.info("cluster : {}", c);
			LOG.info("steps {}", n);

			for (int i = 0; i < n - 1; i++) {

				Double rd = rad * (i * step);
				Double rdpn = rad * ((i + 1) * step);

				LOG.info("up bndry {} low bndry {}", rd, rdpn);

				Double countedPointsIn = Long.valueOf(c.getPoints().stream().filter(e -> {
					Double dist = ClusterUtil.calculateDistance(List.of(c.getCoords()), List.of(e));
					return dist >= rd && dist < rdpn - ((rad * step) / 2);
				}).count()).doubleValue();
				Double countedPointsOut = Long.valueOf(c.getPoints().stream().filter(e -> {
					Double dist = ClusterUtil.calculateDistance(List.of(c.getCoords()), List.of(e));
					return dist >= rdpn - ((rad * step) / 2) && dist <= rdpn;
				}).count()).doubleValue();

				ret.add(new LinearFunction(Pair.of(rd, countedPointsIn), Pair.of(rdpn, countedPointsOut)));
			}
		} else {
			ret.add(new DotFunction((double) c.getPoints().size()));
		}

		return ret;
	}

	private Double findClusterMaxRadius(Cluster c) {
		Double max = -1d;
		for (Double[] pt : c.getPoints()) {
			Double dist = ClusterUtil.calculateDistance(List.of(c.getCoords()), List.of(pt));
			if (dist > max) {
				max = dist;
			}
		}

		return max;
	}

	private List<Pair<String, List<Double>>> determineCharacteristicsList(String s, CharachteristicsEnum... chars) {
		List<Record> records = recordDao.findAllByRequestServiceRelated(s);
		return records.stream().map(e -> {
			return Pair.of(e.getRecordName(), Stream.of(chars).map(ch -> {
				return ClusterUtil.determineValue(e, ch);
			}).collect(Collectors.toList()));
		}).collect(Collectors.toList());
	}

	private Cluster initalCentroidFor(String s, List<Pair<String, List<Double>>> vals, int dims) {
		Double[] coords = new Double[dims];
		int accum = 0;
		for (Pair<String, List<Double>> val : vals.stream().filter(e -> e.getFirst().contentEquals(s))
				.collect(Collectors.toList())) {
			for (int i = 0; i < dims; i++) {
				if (coords[i] == null) {
					coords[i] = 0d;
				}

				coords[i] += val.getSecond().get(i);
			}
			++accum;
		}

		for (int i = 0; i < dims; i++) {
			coords[i] = (coords[i] / accum) + Math.random() * 10;
		}

		return Cluster.builder().name(s).coords(coords).distributionGradient(null).points(null).meanDistr(0d).build();
	}

	private Cluster nearestCentroid(Pair<String, List<Double>> rec, List<Cluster> centroids) {
		double minimumDistance = Double.MAX_VALUE;
		Cluster nearest = null;
		for (Cluster centroid : centroids) {
			double currentDistance = ClusterUtil.calculateDistance(centroid, rec);
			if (currentDistance < minimumDistance) {
				minimumDistance = currentDistance;
				nearest = centroid;
			}
		}

		return nearest;
	}

	private List<Cluster> relocateCentroids(List<Cluster> clusters) {
		return clusters.stream().map(e -> {

			return average(e, e.getPoints());

		}).collect(Collectors.toList());
	}

	private Cluster average(Cluster centroid, List<Double[]> records) {
		if (records != null && !records.isEmpty()) {

			Cluster c = centroid.clone();

			Double suml = 0d;
			Double sumr = 0d;
			for (Double[] rec : records) {
				suml += rec[0];
				sumr += rec[1];
			}

			c.setCoords(new Double[] { suml / records.size(), sumr / records.size() });

			return c;
		}

		return centroid.clone();
	}
}
