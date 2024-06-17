package com.kmarinov.serialize.config;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;

public interface RequestToMethodBindingBean {
	String getPathByMethodName(String methodName);
	Method getMethodViaURI(String uri, String method);
	Class<?> getRequestBodyViaURI(String uri, String method);
	Set<String> getAllMappedMethods();
}
