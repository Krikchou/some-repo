package com.kmarinov.serialize.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("services")
public class ConfigProperties {
	private Integer centroidLimit;
	private Integer centroidsPerClass;
	private Integer maxIterations;
	private Double percentTolerance;
	private List<String> configurationEnabledFor;
	private List<String> apiPackages;

}
