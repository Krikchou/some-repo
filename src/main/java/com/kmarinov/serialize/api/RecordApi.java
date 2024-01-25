package com.kmarinov.serialize.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kmarinov.serialize.alogithm.RecordService;

@RestController("/api/record")
public class RecordApi {
	
	@Autowired
	private RecordService recordService;

	@PostMapping("/number/bulk")
	public void importBulkRecords(@RequestBody NumberRecordBulkInputBean input) {
		recordService.importBulkRecordNumeric(input);
	}
	
	@PostMapping("/string/bulk")
	public void importBulkRecords(@RequestBody StringRecordBulkInputBean input) {
		recordService.importBulkRecordString(input);
	}
	
	
}
