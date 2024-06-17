package com.kmarinov.serialize.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmarinov.serialize.alogithm.DeserializerApiUtils;
import com.kmarinov.serialize.alogithm.ModelService;
import com.kmarinov.serialize.alogithm.RecordService;
import com.kmarinov.serialize.alogithm.RequestService;
import com.kmarinov.serialize.config.RequestToMethodBindingBean;
import com.kmarinov.serialize.entities.Request;
import com.kmarinov.serialize.util.MappingUtils;

@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	public static final Logger LOG = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

	@Autowired DeserializerApiUtils apiUtils;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ContentCachingRequestWrapper nativeRequest = (ContentCachingRequestWrapper) ((ServletWebRequest) request)
				.getNativeRequest();
		LOG.info("PROCESS ERRONIUOS REQUEST");

		apiUtils.process(nativeRequest.getRequestURI(), nativeRequest.getMethod(), nativeRequest.getContentAsByteArray());

		return super.handleHttpMessageNotReadable(ex, headers, status, request);
	}
}