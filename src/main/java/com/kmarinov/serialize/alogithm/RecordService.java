package com.kmarinov.serialize.alogithm;

import com.kmarinov.serialize.api.NumberRecordBulkInputBean;
import com.kmarinov.serialize.api.StringRecordBulkInputBean;

public interface RecordService {
	void importBulkRecordNumeric(NumberRecordBulkInputBean input);

	void importBulkRecordString(StringRecordBulkInputBean input);

	String fitStringForService(String service, String content);
}
