package com.kmarinov.serialize.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kmarinov.serialize.enums.DifferentialsEnum;
import com.kmarinov.serialize.enums.RequestStateEnum;

public class MappingUtils {

	public static final Logger LOG = LoggerFactory.getLogger(MappingUtils.class);

	public static final List<String> booleanConstants = List.of(Boolean.TRUE.toString(), Boolean.FALSE.toString(),
			"TRUE", "FALSE");

	private MappingUtils() {
		// hide implicit constructor
	};

	@SuppressWarnings("unchecked")
	public static RequestStateEnum calculateState(Map<String, Pair<?, ?>> map, Class<?> expectedMetadata,
			Map<String, Object> recievedMetadata) {
		boolean warn = false;
		boolean error = false;

		Map<String, Field> fields = Objects.isNull(expectedMetadata) ? new HashMap<>()
				: remapFields(expectedMetadata.getDeclaredFields());

		for (Entry<String, Pair<?, ?>> entry : map.entrySet()) {
			if (entry.getValue().getFirst() instanceof DifferentialsEnum
					&& DifferentialsEnum.TYPE_ENUM.compareTo((DifferentialsEnum) entry.getValue().getFirst()) == 0
					&& recievedMetadata.get(entry.getKey()) instanceof String) {
				LOG.info("type enum with string");
				error = error || Arrays.stream(fields.get(entry.getKey()).getType().getEnumConstants())
						.anyMatch(e -> !e.toString().equalsIgnoreCase((String) recievedMetadata.get(entry.getKey())));

			} else if (entry.getValue().getFirst() instanceof DifferentialsEnum
					&& DifferentialsEnum.TYPE_BOOLEAN.compareTo((DifferentialsEnum) entry.getValue().getFirst()) == 0) {
				if (recievedMetadata.get(entry.getKey()) instanceof String) {
					LOG.info("type boolean with string");
					error = error || booleanConstants.contains(recievedMetadata.get(entry.getKey()));
				} else if (recievedMetadata.get(entry.getKey()) instanceof Number) {
					LOG.info("type boolean with number");
					error = error || (recievedMetadata.get(entry.getKey()).equals(1)
							|| recievedMetadata.get(entry.getKey()).equals(0));
				}
			} else if (entry.getValue().getFirst() instanceof DifferentialsEnum) {
				if (((DifferentialsEnum) entry.getValue().getFirst())
						.compareTo((DifferentialsEnum) entry.getValue().getSecond()) != 0) {
					LOG.info("types are different");
					if (DifferentialsEnum.NULL.compareTo((DifferentialsEnum) entry.getValue().getSecond()) == 0) {
						LOG.info("recieved type null");
						warn = true;
					} else if (DifferentialsEnum.MISSING
							.compareTo((DifferentialsEnum) entry.getValue().getFirst()) == 0) {
						LOG.info("expected type missing");
						error = true;
						break;
					} else {
						error = true;
						break;
					}
				}
			} else if (entry.getValue().getFirst() instanceof Pair) {
				Pair<?, ?> pair = (Pair<?, ?>) entry.getValue().getFirst();
				if (DifferentialsEnum.MISSING.compareTo((DifferentialsEnum) pair.getFirst()) != 0
						&& DifferentialsEnum.NULL.compareTo((DifferentialsEnum) pair.getSecond()) != 0
						&& DifferentialsEnum.TYPE_EMPTY_ARRAY.compareTo((DifferentialsEnum) pair.getSecond()) != 0
						&& DifferentialsEnum.TYPE_ARRAY_NO_TYPE.compareTo((DifferentialsEnum) pair.getFirst()) != 0) {
					if (DifferentialsEnum.TYPE_ENUM_ARRAY.compareTo((DifferentialsEnum) pair.getFirst()) == 0) {
						error = error || Arrays
								.stream(((Class<?>) ((ParameterizedType) fields.get(entry.getKey()).getGenericType())
										.getActualTypeArguments()[0]).getEnumConstants())
								.anyMatch(e -> !e.toString().equalsIgnoreCase(
										(String) ((List) recievedMetadata.get(entry.getKey())).get(0)));
					} else if (DifferentialsEnum.TYPE_BOOLEAN_ARRAY
							.compareTo((DifferentialsEnum) pair.getFirst()) == 0) {
						if (DifferentialsEnum.TYPE_STRING_ARRAY.compareTo((DifferentialsEnum) pair.getSecond()) == 0) {
							warn = warn || ((List<String>) recievedMetadata.get(entry.getKey())).stream()
									.anyMatch(e -> !booleanConstants.contains((String) e));
						} else if (DifferentialsEnum.TYPE_NUMBER_ARRAY
								.compareTo((DifferentialsEnum) pair.getSecond()) == 0) {
							warn = warn || ((List<Number>) recievedMetadata.get(entry.getKey())).stream()
									.anyMatch(e -> !List.of(0, 1).contains((Number) e));
						} else if (DifferentialsEnum.TYPE_OBJECT_ARRAY
								.compareTo((DifferentialsEnum) pair.getFirst()) == 0
								&& DifferentialsEnum.TYPE_OBJECT_ARRAY
										.compareTo((DifferentialsEnum) pair.getSecond()) == 0) {
							RequestStateEnum state = calculateState(
									(Map<String, Pair<?, ?>>) entry.getValue().getSecond(),
									(Class<?>) ((ParameterizedType) fields.get(entry.getKey()).getGenericType())
											.getActualTypeArguments()[0],
									(Map<String, Object>) ((List<?>) recievedMetadata.get(entry.getKey())).get(0));

							switch (state) {
							case ERROR:
								error = true;
								break;
							case WARN:
								warn = true;
								break;
							default:
								break;
							}

						} else if (DifferentialsEnum.TYPE_OBJECT.compareTo((DifferentialsEnum) pair.getFirst()) == 0
								&& DifferentialsEnum.TYPE_OBJECT.compareTo((DifferentialsEnum) pair.getSecond()) == 0) {
							RequestStateEnum state = calculateState(
									(Map<String, Pair<?, ?>>) entry.getValue().getSecond(),
									fields.get(entry.getKey()).getType(),
									(Map<String, Object>) recievedMetadata.get(entry.getKey()));

							switch (state) {
							case ERROR:
								error = true;
								break;
							case WARN:
								warn = true;
								break;
							default:
								break;
							}

						} else {
							error = true;
							break;
						}
					}
				}
			}
		}

		if (error) {
			return RequestStateEnum.ERROR;
		} else if (warn) {
			return RequestStateEnum.WARN;
		} else {
			return RequestStateEnum.OK;
		}

	}

	/**
	 * Object could be an instance of Pair or Map
	 * 
	 * Left value (first) is the Class Metamodel, Right value is the received
	 * metamodel
	 */
	public static Map<String, Pair<?, ?>> compareMetamodel(Class<?> expectedMetadata, Object recievedMetadata) {

		Map<String, Pair<?, ?>> returnMap = new HashMap<>();

		Set<String> names = recievedMetadata instanceof Map ? ((Map<String, Object>) recievedMetadata).keySet()
				: Set.of("0");

		Map<String, Field> fields = Objects.isNull(expectedMetadata) ? new HashMap<>()
				: remapFields(expectedMetadata.getDeclaredFields());

		Set<String> expectedFields = fields.keySet();

		for (String name : names) {
			Field f = fields.get(name);
			Object o = recievedMetadata instanceof Map ? ((Map<String, Object>) recievedMetadata).get(name)
					: ((List<Object>) recievedMetadata).get(0);

			if (Objects.nonNull(f)) {
				Pair<DifferentialsEnum, DifferentialsEnum> res = compareFields(o, f);
				if (res.getFirst().compareTo(DifferentialsEnum.TYPE_OBJECT) == 0
						&& res.getSecond().compareTo(DifferentialsEnum.TYPE_OBJECT) == 0) {
					returnMap.put(name, Pair.of(res, compareMetamodel(f.getDeclaringClass(), (Map<String, Object>) o)));
				} else if (res.getFirst().compareTo(DifferentialsEnum.TYPE_OBJECT_ARRAY) == 0
						&& res.getSecond().compareTo(DifferentialsEnum.TYPE_OBJECT_ARRAY) == 0) {
					if (((List<?>) o).get(0) instanceof List) {
						int depth = 0;
						Map<String, Object> endClass = runThroughLists((List<?>) ((List<?>) o).get(0), depth);
						returnMap.put(name,
								Pair.of(res, compareMetamodel(
										(Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0],
										(List<Object>) ((List<?>) o).get(0))));

					} else {
						returnMap.put(name,
								Pair.of(res, compareMetamodel(
										(Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0],
										(Map<String, Object>) ((List<?>) o).get(0))));
					}
				} else {
					returnMap.put(name, res);
				}
			} else {
				DifferentialsEnum type = determineType(o);

				if (DifferentialsEnum.TYPE_OBJECT.compareTo(type) == 0) {
					returnMap.put(name, Pair.of(Pair.of(DifferentialsEnum.MISSING, type),
							compareMetamodel(null, (Map<String, Object>) o)));
				} else if (DifferentialsEnum.TYPE_OBJECT_ARRAY.compareTo(type) == 0) {
					returnMap.put(name,
							Pair.of(Pair.of(DifferentialsEnum.MISSING, type),
									compareMetamodel(null,
											((List<?>) o).get(0) instanceof List ? ((List<?>) ((List<?>) o).get(0))
													: (Map<String, Object>) ((List<?>) o).get(0))));
				} else {
					returnMap.put(name, Pair.of(DifferentialsEnum.MISSING, type));
				}
			}
		}

		expectedFields.removeAll(names);

		for (String name : expectedFields) {
			Field f = fields.get(name);
			DifferentialsEnum type = determineType(f);

			if (DifferentialsEnum.TYPE_OBJECT.compareTo(type) == 0) {
				returnMap.put(name, Pair.of(Pair.of(type, DifferentialsEnum.MISSING),
						compareMetamodel(f.getType(), new HashMap<>())));
			} else if (DifferentialsEnum.TYPE_OBJECT_ARRAY.compareTo(type) == 0) {
				returnMap.put(name,
						Pair.of(Pair.of(type, DifferentialsEnum.MISSING),
								compareMetamodel(
										(Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0],
										new HashMap<>())));
			} else {
				returnMap.put(name, Pair.of(type, DifferentialsEnum.MISSING));
			}
		}

		LOG.info("Info {}", returnMap);

		return returnMap;
	}

	public static Map<String, Field> remapFields(Field[] fields) {
		return Arrays.stream(fields).collect(Collectors.toMap(e -> {
			JsonProperty p = e.getAnnotation(JsonProperty.class);
			if (p != null && p.namespace() != null && !p.namespace().contentEquals("")) {
				return p.namespace();
			}

			return e.getName();
		}, e -> {
			return e;
		}));
	}

	public static Map<String, Object> runThroughLists(List<?> l, int i) {
		if (l.get(0) instanceof List) {
			return runThroughLists((List<?>) l.get(0), i++);
		} else {
			return (Map<String, Object>) l.get(0);
		}
	}

	private static Pair<DifferentialsEnum, DifferentialsEnum> compareFields(Object recieved, Field expected) {
		return Pair.of(determineType(expected), determineType(recieved));
	}

	public static DifferentialsEnum determineType(Object o) {
		if (Objects.isNull(o)) {
			return DifferentialsEnum.NULL;
		}

		if (o instanceof List) {
			if (((List<?>) o).isEmpty()) {
				return DifferentialsEnum.TYPE_EMPTY_ARRAY;
			} else {
				List<?> inst = (List<?>) o;

				if (Number.class.isAssignableFrom(inst.get(0).getClass())) {
					return DifferentialsEnum.TYPE_NUMBER_ARRAY;
				} else if (inst.get(0) instanceof String) {
					return DifferentialsEnum.TYPE_STRING_ARRAY;
				} else {
					return DifferentialsEnum.TYPE_OBJECT_ARRAY;
				}
			}
		} else {
			if (Number.class.isAssignableFrom(o.getClass())) {
				return DifferentialsEnum.TYPE_NUMBER;
			} else if (String.class.isAssignableFrom(o.getClass())) {
				return DifferentialsEnum.TYPE_STRING;
			} else {
				return DifferentialsEnum.TYPE_OBJECT;
			}
		}
	}

	public static DifferentialsEnum determineType(Field o) {
		if (List.class.isAssignableFrom(o.getType())) {
			ParameterizedType tr = (ParameterizedType) o.getGenericType();
			Type[] types = tr.getActualTypeArguments();
			if (types[0] instanceof ParameterizedType) {
				return determineType(types[0]);
			} else {
				if (types.length != 0) {
					if (Number.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_NUMBER_ARRAY;
					} else if (String.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_STRING_ARRAY;
					} else if (Boolean.class.isAssignableFrom((Class<?>) types[0])
							|| boolean.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_BOOLEAN_ARRAY;

					} else if (((Class<?>) types[0]).isEnum()) {
						return DifferentialsEnum.TYPE_ENUM_ARRAY;

					} else {
						return DifferentialsEnum.TYPE_OBJECT_ARRAY;
					}
				} else {
					return DifferentialsEnum.TYPE_ARRAY_NO_TYPE;
				}
			}
		} else {
			if (Number.class.isAssignableFrom(o.getType())) {
				return DifferentialsEnum.TYPE_NUMBER;
			} else if (String.class.isAssignableFrom(o.getType())) {
				return DifferentialsEnum.TYPE_STRING;

			} else if (Boolean.class.isAssignableFrom(o.getType()) || boolean.class.isAssignableFrom(o.getType())) {
				return DifferentialsEnum.TYPE_BOOLEAN;

			} else if (o.getType().isEnum()) {
				return DifferentialsEnum.TYPE_ENUM;

			} else {
				return DifferentialsEnum.TYPE_OBJECT;
			}
		}

	}

	public static DifferentialsEnum determineType(Type o) {
		if (o instanceof ParameterizedType
				&& List.class.isAssignableFrom((Class<?>) ((ParameterizedType) o).getRawType())) {
			ParameterizedType tr = (ParameterizedType) o;
			Type[] types = tr.getActualTypeArguments();
			if (types[0] instanceof ParameterizedType) {
				return determineType(types[0]);
			} else {
				if (types.length != 0) {
					if (Number.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_NUMBER_ARRAY;
					} else if (String.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_STRING_ARRAY;
					} else if (Boolean.class.isAssignableFrom((Class<?>) types[0])
							|| boolean.class.isAssignableFrom((Class<?>) types[0])) {
						return DifferentialsEnum.TYPE_BOOLEAN_ARRAY;

					} else if (((Class<?>) types[0]).isEnum()) {
						return DifferentialsEnum.TYPE_ENUM_ARRAY;

					} else {
						return DifferentialsEnum.TYPE_OBJECT_ARRAY;
					}
				} else {
					return DifferentialsEnum.TYPE_ARRAY_NO_TYPE;
				}
			}
		} else {
			if (Number.class.isAssignableFrom((Class<?>) o)) {
				return DifferentialsEnum.TYPE_NUMBER;
			} else if (String.class.isAssignableFrom((Class<?>) o)) {
				return DifferentialsEnum.TYPE_STRING;

			} else if (Boolean.class.isAssignableFrom((Class<?>) o) || boolean.class.isAssignableFrom((Class<?>) o)) {
				return DifferentialsEnum.TYPE_BOOLEAN;

			} else if (((Class<?>) o).isEnum()) {
				return DifferentialsEnum.TYPE_ENUM;

			} else {
				return DifferentialsEnum.TYPE_OBJECT;
			}
		}

	}

}
