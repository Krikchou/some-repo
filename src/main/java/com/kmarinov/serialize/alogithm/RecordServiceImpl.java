package com.kmarinov.serialize.alogithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.api.NumberRecordBulkInputBean;
import com.kmarinov.serialize.api.StringRecordBulkInputBean;
import com.kmarinov.serialize.config.ConfigProperties;
import com.kmarinov.serialize.dao.NumberRecordRepository;
import com.kmarinov.serialize.dao.StringRecordRepository;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.NumberRecord;
import com.kmarinov.serialize.model.StringRecord;
import com.kmarinov.serialize.util.CentroidDensityPair;
import com.kmarinov.serialize.util.ClusterUtil;

@Service
public class RecordServiceImpl implements RecordService {
	
	@Autowired
	private ConfigProperties config;

	@Autowired
	private NumberRecordRepository numberDao;

	@Autowired
	private StringRecordRepository stringDao;

	@Autowired
	private ModelService modelService;

	@Override
	public void importBulkRecordNumeric(NumberRecordBulkInputBean input) {
		numberDao.saveAll(input.getValues().stream().map(e -> {
			NumberRecord entity = new NumberRecord();
			entity.setRecordName(input.getRecordName());
			entity.setServiceRelated(input.getServiceRelated());
			entity.setValue(BigDecimal.valueOf(e));
			entity.setWholeNumber(e % 1 == 0);
			return entity;
		}).collect(Collectors.toList()));
	}

	@Override
	public void importBulkRecordString(StringRecordBulkInputBean input) {
		stringDao.saveAll(input.getValues().stream().map(e -> {
			StringRecord entity = new StringRecord();
			entity.setLength(e.length());
			entity.setRecordName(input.getRecordName());
			entity.setServiceRelated(input.getServiceRelated());
			entity.setContainsWords(this.doesContainEmptyChars(e));
			entity.setValue(e);
			if (!doesContainSpecialChars(e) && !doesContainEmptyChars(e)) {
				entity.setPattern(ClusterUtil.generateStringPattern(e));
			}
			return entity;
		}).collect(Collectors.toList()));
	}

	@Override
	public String fitStringForService(String service, String content) {
		ClusterModel model1 = modelService.generateClusterModelForServiceOnCharacteristicPairs(service, CharachteristicsEnum.SIZE,
				CharachteristicsEnum.WORD_COUNT);
		
		ClusterModel model2 = modelService.generateClusterModelForServiceOnCharacteristicPairs(service, CharachteristicsEnum.SIZE,
				CharachteristicsEnum.PATTERN);
		
		List<CentroidDensityPair> pair1 = calculateFirstNCentroids(model1,
				Pair.of(ClusterUtil.determineValue(content, CharachteristicsEnum.SIZE), ClusterUtil.determineValue(content, CharachteristicsEnum.WORD_COUNT)));
		
		List<CentroidDensityPair> pair2 = calculateFirstNCentroids(model2,
				Pair.of(ClusterUtil.determineValue(content, CharachteristicsEnum.WORD_COUNT), ClusterUtil.determineValue(content, CharachteristicsEnum.PATTERN)));
		List<Pair<String, Integer>> cumulativeSums = new ArrayList<>();
		for(int i=0;i<pair1.size();i++) {
			for(int j=0;j<pair2.size();j++) {
				if (pair1.get(i).getForProperty().equals(pair2.get(j).getForProperty())) {
					cumulativeSums.add(Pair.of(pair1.get(i).getForProperty(),
							(i+j)/2));
					break;
				}
			}
		}
		
		Integer currVal = Integer.MAX_VALUE;
		String res = "";
		
		for(Pair<String, Integer> p : cumulativeSums) {
			System.out.println(p.getFirst() + " " + p.getSecond());
			if (p.getSecond() < currVal) {
				currVal = p.getSecond();
				res = p.getFirst();
			}
		}
		
		return res;
	}

	private Boolean doesContainEmptyChars(String testString) {
		return testString.contains(" ");
	}

	private Boolean doesContainSpecialChars(String testString) {
		return testString.matches("[^a-zA-Z0-9]+");
	}
	
	private List<CentroidDensityPair> calculateFirstNCentroids(ClusterModel model, Pair<Double, Double> recordToFit) {
		System.out.println(recordToFit);
		List<CentroidDensityPair> pairs =  new ArrayList<>();
		List<CentroidDensityPair> workignWith = new ArrayList<>(model.getCentroids());
		int len = workignWith.size() <= config.getCentroidLimit() ? workignWith.size() : config.getCentroidLimit();
		for(int i=0;i<len;i++) {
			CentroidDensityPair ret = ClusterUtil.nearestCentroid(recordToFit, workignWith);
			pairs.add(ret);
			workignWith.remove(ret);
		}
		
		System.out.println(pairs);
		
		return pairs;
		
	}

}
