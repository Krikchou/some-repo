package com.kmarinov.serialize.alogithm;

import java.util.Map;

import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.DistributionModel;

public interface ModelService {
	public Map<String, DistributionModel> generateDistributionModelsForService(String service);
	
	public Map<String,Map<String, DistributionModel>> generateDistributionModelsForServices();
	
	public ClusterModel generateClusterModelForServiceOnCharacteristicPairs(String s, CharachteristicsEnum char1, CharachteristicsEnum char2);
}
