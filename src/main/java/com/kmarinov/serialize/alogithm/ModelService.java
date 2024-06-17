package com.kmarinov.serialize.alogithm;

import java.util.List;
import java.util.Map;

import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.DistributionModel;

public interface ModelService {
	public Map<String, DistributionModel> generateDistributionModelsForService(String service);
	
	public Map<String,Map<String, DistributionModel>> generateDistributionModelsForServices();
	
	public List<ClusterDto> generateClusterModelForServiceOnCharacteristics(String s, CharachteristicsEnum... chars);
}
