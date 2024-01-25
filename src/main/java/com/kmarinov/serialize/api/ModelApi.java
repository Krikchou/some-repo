package com.kmarinov.serialize.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kmarinov.serialize.alogithm.ModelService;
import com.kmarinov.serialize.alogithm.RecordService;
import com.kmarinov.serialize.model.DistributionModel;

@RestController("/api/models")
public class ModelApi {
	
	@Autowired 
	private ModelService modelService;
	
	@Autowired
	private RecordService recordService;
	
	@GetMapping("/{serviceName}")
	public @ResponseBody Map<String, DistributionModel> genDistModelOf(@RequestParam String serviceName) {
		return modelService.generateDistributionModelsForService(serviceName);
	}
	
	@GetMapping("fit/{serviceName}/{value}")
	public @ResponseBody String fitValue(@RequestParam String serviceName, @RequestParam String value) {
		return recordService.fitStringForService(serviceName, value);
	}
	
	
}
