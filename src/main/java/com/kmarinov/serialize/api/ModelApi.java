package com.kmarinov.serialize.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kmarinov.serialize.alogithm.ModelService;
import com.kmarinov.serialize.alogithm.RecordService;
import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.model.DistributionModel;

@RestController
@RequestMapping("/api/models")
public class ModelApi {
	
	public static Logger LOG = LoggerFactory.getLogger(ModelApi.class);
	
	@Autowired 
	private ModelService modelService;
	
	@Autowired
	private RecordService recordService;
	
	@GetMapping("/dist/{serviceName}")
	public @ResponseBody Map<String, DistributionModel> genDistModelOf(@PathVariable String serviceName) {
		return modelService.generateDistributionModelsForService(serviceName);
	}
	
	@GetMapping("/fit/{serviceName}/{value}")
	public @ResponseBody String fitValue(@PathVariable String serviceName, @PathVariable String value) {
		return recordService.fitStringForService(serviceName, value);
	}
	
	@GetMapping("/cluster/{serviceName}")
	public @ResponseBody List<ClusterDto> model(@PathVariable String serviceName) {
		return modelService.generateClusterModelForServiceOnCharacteristics(serviceName, CharachteristicsEnum.values());
	}
	
	@PostMapping("/recalculate")
	public void recalculate() {
		
	}
	
	@PostMapping("/dummy")
	public ResponseEntity<String> dummyTestMapping(@RequestBody DummyInputBean body) {
		LOG.info("Data : {}", body);
		return ResponseEntity.ok("OK");
	}
	
	
}
