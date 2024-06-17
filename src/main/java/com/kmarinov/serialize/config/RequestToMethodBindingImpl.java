package com.kmarinov.serialize.config;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RequestToMethodBindingImpl implements RequestToMethodBindingBean {
	private Map<String, Method> methodBinding;

	@Override
	public Method getMethodViaURI(String uri, String method) {
		return methodBinding.get(method + ":" + patternMatch(uri));
	}

	@Override
	public Class<?> getRequestBodyViaURI(String uri, String method) {
		Optional<Parameter> p = Arrays.stream(methodBinding.get(method + ":" + patternMatch(uri)).getParameters())
				.filter(e -> e.isAnnotationPresent(RequestBody.class)).findFirst();

		if (p.isPresent()) {
			return p.get().getType();
		} else {
			return null;
		}
	}

	private String patternMatch(String uri) {
		return methodBinding.keySet().stream().map(e -> e.split(":")[1]).filter(e -> {
			String[] uriArr = uri.split("/");
			String[] tmpArr = e.split("/");

			if (uriArr.length == tmpArr.length) {
				boolean ret = true;
				Pattern URL_PATTERN = Pattern.compile("\\{[a-zA-Z0-9]+\\}");
				for (int i = 0; i < uriArr.length; i++) {
					if (URL_PATTERN.matcher(tmpArr[i]).find()) {
						ret = ret && true;
					} else if (tmpArr[i].contentEquals(uriArr[i])) {
						ret = ret && true;
					} else {
						ret = ret && false;
						break;
					}
				}

				return ret;
			}

			return false;
		}).findFirst().get();
	}

	@Override
	public String getPathByMethodName(String methodName) {
		return methodBinding.entrySet().stream().filter(e -> {
			return e.getValue().getName().contentEquals(methodName);
		}).map(e -> e.getKey()).findFirst().orElse(null);
	}

	@Override
	public Set<String> getAllMappedMethods() {
		return methodBinding.entrySet().stream().map(e -> e.getValue().getName()).collect(Collectors.toSet());
	}

}
