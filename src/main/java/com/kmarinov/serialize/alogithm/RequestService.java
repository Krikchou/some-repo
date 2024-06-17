package com.kmarinov.serialize.alogithm;

import com.kmarinov.serialize.entities.Request;

public interface RequestService {
	Request create(String serviceRelated, String body);
	Request create(String serviceRelated, String body, String apiPath);
}
