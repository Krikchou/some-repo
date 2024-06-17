package com.kmarinov.serialize.config;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.kmarinov.serialize.alogithm.DeserializerApiUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RequestFilter implements Filter {

	@Autowired
	DeserializerApiUtils apiUtils;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest requestToUse = (HttpServletRequest) request;
		ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(requestToUse);

		filterChain.doFilter(contentCachingRequestWrapper, response);
	}
}
