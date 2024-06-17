package com.kmarinov.serialize.alogithm;

import java.util.Map;

import com.kmarinov.serialize.api.NumberRecordBulkInputBean;
import com.kmarinov.serialize.api.StringRecordBulkInputBean;
import com.kmarinov.serialize.entities.Request;

public interface RecordService {
	void importBulkRecordNumeric(NumberRecordBulkInputBean input);

	void importBulkRecordString(StringRecordBulkInputBean input);

	String fitStringForService(String service, String content);
	
	com.kmarinov.serialize.entities.Record save(com.kmarinov.serialize.entities.Record rec);
	
	boolean bulkSaveRecords(Map<String, Object> map, Request req, com.kmarinov.serialize.entities.Record rec, Class<?> expectedClass);
}
