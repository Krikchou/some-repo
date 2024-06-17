package com.kmarinov.serialize.alogithm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmarinov.serialize.api.CustomRestExceptionHandler;
import com.kmarinov.serialize.config.RequestToMethodBindingBean;
import com.kmarinov.serialize.entities.Request;
import com.kmarinov.serialize.util.MappingUtils;

@Service
public class DeserializerUtils implements DeserializerApiUtils {
	
	public static final Logger LOG = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

	@Autowired
	RequestToMethodBindingBean requestToMethodBindingBean;

	@Autowired
	ModelService models;

	@Autowired
	RecordService recordService;

	@Autowired
	RequestService requestService;
	
	public void process(String uri, String method, byte[] content) {
		String requestEntityAsString = new String(content);

		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> map = mapper.readValue(requestEntityAsString, new TypeReference<>() {
			});
			Class<?> rqstBdy = requestToMethodBindingBean.getRequestBodyViaURI(uri,
					method);
			Map<String, Pair<?, ?>> comparisonMap = MappingUtils.compareMetamodel(rqstBdy, map);
			LOG.info("map : {}, class : {}, result : {} ", map, rqstBdy.getName(),
					MappingUtils.calculateState(comparisonMap, rqstBdy, map));
			
			Request req = requestService.create(requestToMethodBindingBean.getMethodViaURI(uri,
					method).getName(), requestEntityAsString);
			
			req.setState(MappingUtils.calculateState(comparisonMap, rqstBdy, map));		
			
			recordService.bulkSaveRecords(map, req, null, rqstBdy);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}
