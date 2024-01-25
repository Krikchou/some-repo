package com.kmarinov.serialize.api;

import java.util.List;

import lombok.Data;

@Data
public class NumberRecordBulkInputBean {
	private String serviceRelated;
	private String recordName;
	private List<Double> values;
}
