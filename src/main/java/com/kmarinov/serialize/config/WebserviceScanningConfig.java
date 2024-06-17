package com.kmarinov.serialize.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class WebserviceScanningConfig {

	private static final Logger LOG = LoggerFactory.getLogger(WebserviceScanningConfig.class);

	@Bean
	RequestToMethodBindingBean requestToMethodBindingBean(@Autowired ConfigProperties config) {
		LOG.info("Start scanning API's");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

		Map<String, Method> methods = new HashMap<>();

		for (String pckg : config.getApiPackages()) {
			LOG.info("Scanning package : {}", pckg);
			Set<BeanDefinition> beans = provider.findCandidateComponents(pckg);
			beans.stream().forEach(e -> {
				LOG.info("Scanning class : {}", e.getBeanClassName());
				Class<?> c = e.getResolvableType().getRawClass();
				if (c == null) {
					try {
						c = Class.forName(e.getBeanClassName());
					} catch (ClassNotFoundException e1) {
						LOG.warn("Class not resolvable");
					}
				}
				if (c != null) {
					RequestMapping annotation = c.getAnnotation(RequestMapping.class);
					if (annotation != null) {
						final String[] path = ArrayUtils.addAll(annotation.path(), annotation.value());

						LOG.info("Base paths : {}", Arrays.toString(path));

						Arrays.stream(c.getMethods()).forEach(m -> {
							LOG.info("Scan method : {}", m.getName());
							GetMapping gm = m.getAnnotation(GetMapping.class);
							PutMapping putm = m.getAnnotation(PutMapping.class);
							PostMapping postm = m.getAnnotation(PostMapping.class);
							DeleteMapping dm = m.getAnnotation(DeleteMapping.class);
							RequestMapping rqm = m.getAnnotation(RequestMapping.class);

							LOG.debug("Method mapping present : GET{}, PUT{}, POST{}, DELETE{}, REQUEST_MAPPING{}",
									gm != null, putm != null, postm != null, dm != null, rqm != null);

							if (Arrays.stream(m.getParameters())
									.anyMatch(param -> param.isAnnotationPresent(RequestBody.class))) {
								LOG.debug("Has @RequestBody");
								if (gm != null) {
									Arrays.stream(path != null && path.length != 0 ? path : new String[] { "" })
											.flatMap(s1 -> Arrays.stream(ArrayUtils.addAll(gm.path(), gm.value()))
													.map(s2 -> s1 + s2))
											.forEach(str -> methods.put(HttpMethod.GET.name() + ":" + str, m));

								} else if (putm != null) {
									Arrays.stream(path != null && path.length != 0 ? path : new String[] { "" })
											.flatMap(s1 -> Arrays.stream(ArrayUtils.addAll(putm.path(), putm.value()))
													.map(s2 -> s1 + s2))
											.forEach(str -> methods.put(HttpMethod.PUT.name() + ":" + str, m));

								} else if (postm != null) {
									Arrays.stream(path != null && path.length != 0 ? path : new String[] { "" })
											.flatMap(s1 -> Arrays.stream(ArrayUtils.addAll(postm.path(), postm.value()))
													.map(s2 -> s1 + s2))
											.forEach(str -> {
												LOG.info("PATH {}", str);
												methods.put(HttpMethod.POST.name() + ":" + str, m);
											});

								} else if (dm != null) {
									Arrays.stream(path != null && path.length != 0 ? path : new String[] { "" })
											.flatMap(s1 -> Arrays.stream(ArrayUtils.addAll(dm.path(), dm.value()))
													.map(s2 -> s1 + s2))
											.forEach(str -> methods.put(HttpMethod.DELETE.name() + ":" + str, m));

								} else if (rqm != null) {
									Arrays.stream(path != null && path.length != 0 ? path : new String[] { "" })
											.flatMap(s1 -> Arrays.stream(ArrayUtils.addAll(rqm.path(), rqm.value()))
													.map(s2 -> s1 + s2))
											.forEach(str -> Arrays.stream(rqm.method())
													.forEach(mthd -> methods.put(mthd.name() + ":" + str, m)));

								}
							}

						});
					}
				}
			});
		}

		LOG.debug("Methods config : {}", methods);

		return new RequestToMethodBindingImpl(methods);
	}
}
