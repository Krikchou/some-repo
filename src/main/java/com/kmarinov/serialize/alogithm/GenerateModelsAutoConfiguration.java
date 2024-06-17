package com.kmarinov.serialize.alogithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kmarinov.serialize.config.RequestToMethodBindingBean;
import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.enums.CharachteristicsEnum;

@Configuration
public class GenerateModelsAutoConfiguration {
	
	public static final Logger LOG = LoggerFactory.getLogger(GenerateModelsAutoConfiguration.class);
	
	@Bean
	public Map<String, List<ClusterDto>> clustersBean(@Autowired RequestToMethodBindingBean methodBindingBean, @Autowired ModelService modelService) {
		Map<String, List<ClusterDto>> returnMap = new HashMap<>();
		for (String s : methodBindingBean.getAllMappedMethods()) {
			LOG.info("Generating model for service : {}", s);
			returnMap.put(s, modelService.generateClusterModelForServiceOnCharacteristics(s, CharachteristicsEnum.values()));
		}
		
		return returnMap;
	}
}
