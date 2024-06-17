package com.kmarinov.serialize.alogithm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.enums.CharachteristicsEnum;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private Map<String, List<ClusterDto>> clustersBean;

	@Autowired
	private ModelService modelService;

	@Override
	public boolean recalculate(String service) {
		if ("ALL".equals(service)) {
			for (String s : clustersBean.keySet()) {
				clustersBean.remove(s);
				clustersBean.put(s,
						modelService.generateClusterModelForServiceOnCharacteristics(s, CharachteristicsEnum.values()));
			}
			return true;
		} else {
			clustersBean.remove(service);
			clustersBean.put(service, modelService.generateClusterModelForServiceOnCharacteristics(service,
					CharachteristicsEnum.values()));
			return true;
		}
	}

}
