package com.kmarinov.serialize.alogithm;

import org.springframework.web.util.ContentCachingRequestWrapper;

public interface DeserializerApiUtils {
	void process(String uri, String method, byte[] content);
}
