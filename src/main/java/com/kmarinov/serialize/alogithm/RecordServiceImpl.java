package com.kmarinov.serialize.alogithm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.api.NumberRecordBulkInputBean;
import com.kmarinov.serialize.api.StringRecordBulkInputBean;
import com.kmarinov.serialize.config.ConfigProperties;
import com.kmarinov.serialize.dao.NumberRecordRepository;
import com.kmarinov.serialize.dao.RecordRepository;
import com.kmarinov.serialize.dao.StringRecordRepository;
import com.kmarinov.serialize.dto.ClusterDto;
import com.kmarinov.serialize.entities.NumberRecord;
import com.kmarinov.serialize.entities.ObjectRecord;
import com.kmarinov.serialize.entities.Request;
import com.kmarinov.serialize.entities.StringRecord;
import com.kmarinov.serialize.enums.CharachteristicsEnum;
import com.kmarinov.serialize.enums.DifferentialsEnum;
import com.kmarinov.serialize.model.ClusterModel;
import com.kmarinov.serialize.util.CentroidDensityPair;
import com.kmarinov.serialize.util.ClusterUtil;
import com.kmarinov.serialize.util.MappingUtils;

@Service
public class RecordServiceImpl implements RecordService {

	@Autowired
	private ConfigProperties config;

	@Autowired
	private NumberRecordRepository numberDao;

	@Autowired
	private RecordRepository recordDao;

	@Autowired
	private StringRecordRepository stringDao;

	@Autowired
	private ModelService modelService;

	private static final Logger LOG = LoggerFactory.getLogger(RecordService.class);

	@Override
	public void importBulkRecordNumeric(NumberRecordBulkInputBean input) {
		numberDao.saveAll(input.getValues().stream().map(e -> {
			NumberRecord entity = new NumberRecord();
			entity.setRecordName(input.getRecordName());
			entity.setServiceRelated(input.getServiceRelated());
			entity.setValue(BigDecimal.valueOf(e));
			entity.setWholeNumber(e % 1 == 0);
			return entity;
		}).collect(Collectors.toList()));
	}

	@Override
	public com.kmarinov.serialize.entities.Record save(com.kmarinov.serialize.entities.Record r) {
		return recordDao.save(r);
	}

	public StringRecord saveStringRecord(String recordName, com.kmarinov.serialize.entities.Record parent, String value,
			Integer indx, Request req) {
		StringRecord r = new StringRecord();
		r.setIndex(indx);
		r.setIsElementInArray(indx != null);
		r.setLength(value == null ? null : value.length());
		r.setParentField(parent);
		r.setPattern(value == null ? null : ClusterUtil.generateStringPattern(value));
		r.setRecordName(recordName + (indx != null ? "_array_value" : ""));
		r.setRequest(req);
		r.setServiceRelated(req.getServiceRelated());
		r.setValue(value);
		return recordDao.save(r);
	}

	public NumberRecord saveNumberRecord(String recordName, com.kmarinov.serialize.entities.Record parent, Number value,
			Integer indx, Request req) {
		NumberRecord r = new NumberRecord();
		r.setIndex(indx);
		r.setIsElementInArray(indx != null);
		r.setParentField(parent);
		r.setRecordName(recordName + (indx != null ? "_array_value" : ""));
		r.setRequest(req);
		r.setServiceRelated(req.getServiceRelated());
		r.setValue(value == null ? null : BigDecimal.valueOf(value.longValue()));
		r.setWholeNumber(value == null ? null : value.doubleValue() % 1 == 0);
		return recordDao.save(r);
	}

	public ObjectRecord saveArrayRecord(String recordName, com.kmarinov.serialize.entities.Record parent, Request req,
			Integer indx) {
		ObjectRecord obj = new ObjectRecord();
		obj.setIndex(indx);
		obj.setIsArray(true);
		obj.setIsElementInArray(indx == null);
		obj.setParentField(parent);
		obj.setRecordName(recordName + (indx != null ? "_array_value" : ""));
		obj.setRequest(req);
		obj.setServiceRelated(req.getServiceRelated());

		return recordDao.save(obj);
	}

	public ObjectRecord saveObjectRecord(String recordName, com.kmarinov.serialize.entities.Record parent, Request req,
			Integer indx) {
		ObjectRecord obj = new ObjectRecord();
		obj.setIndex(indx);
		obj.setIsArray(false);
		obj.setIsElementInArray(indx == null);
		obj.setParentField(parent);
		obj.setRecordName(recordName + (indx != null ? "_array_value" : ""));
		obj.setRequest(req);
		obj.setServiceRelated(req.getServiceRelated());

		return recordDao.save(obj);
	}

	@Override
	public void importBulkRecordString(StringRecordBulkInputBean input) {
		stringDao.saveAll(input.getValues().stream().map(e -> {
			StringRecord entity = new StringRecord();
			entity.setLength(e.length());
			entity.setRecordName(input.getRecordName());
			entity.setServiceRelated(input.getServiceRelated());
			entity.setContainsWords(this.doesContainEmptyChars(e));
			entity.setValue(e);
			if (!doesContainSpecialChars(e) && !doesContainEmptyChars(e)) {
				entity.setPattern(ClusterUtil.generateStringPattern(e));
			}
			return entity;
		}).collect(Collectors.toList()));
	}

	@Override
	public String fitStringForService(String service, String content) {
		List<ClusterDto> models = modelService.generateClusterModelForServiceOnCharacteristics(service,
				CharachteristicsEnum.values());
		List<Double> valToFit = Arrays.stream(CharachteristicsEnum.values())
				.map(e -> ClusterUtil.determineValue(service, e)).toList();
		models.stream().sorted((o1, o2) -> {
			Double o1d = ClusterUtil.calculateDistance(o1.getCentroid(), valToFit);
			Double o2d = ClusterUtil.calculateDistance(o2.getCentroid(), valToFit);

			if (o1d == o2d) {
				return 0;
			} else if (o1d > o2d) {
				return 1;
			} else {
				return -1;
			}
		});

		return models.get(0).getForProperty();
	}

	private Boolean doesContainEmptyChars(String testString) {
		return testString.contains(" ");
	}

	private Boolean doesContainSpecialChars(String testString) {
		return testString.matches("[^a-zA-Z0-9]+");
	}

	private List<CentroidDensityPair> calculateFirstNCentroids(ClusterModel model, Pair<Double, Double> recordToFit) {
		LOG.info(recordToFit.toString());
		List<CentroidDensityPair> pairs = new ArrayList<>();
		List<CentroidDensityPair> workignWith = new ArrayList<>(model.getCentroids());
		int len = workignWith.size() <= config.getCentroidLimit() ? workignWith.size() : config.getCentroidLimit();
		for (int i = 0; i < len; i++) {
			CentroidDensityPair ret = ClusterUtil.nearestCentroid(recordToFit, workignWith);
			pairs.add(ret);
			workignWith.remove(ret);
		}

		LOG.info(pairs.toString());

		return pairs;

	}

	@Override
	public boolean bulkSaveRecords(Map<String, Object> map, Request req, com.kmarinov.serialize.entities.Record parent,
			Class<?> expectedClass) {
		return this.bulkSaveRecords(map, req, parent, expectedClass, null);
	}

	public boolean bulkSaveRecords(Map<String, Object> map, Request req, com.kmarinov.serialize.entities.Record parent,
			Class<?> expectedClass, Integer indx) {
		for (Entry<String, Object> entry : map.entrySet()) {
			DifferentialsEnum type = MappingUtils.determineType(entry.getValue());

			switch (type) {
			case NULL: // from parent type
				if (expectedClass != null) {
					DifferentialsEnum superType = MappingUtils
							.determineType(MappingUtils.remapFields(expectedClass.getFields()).get(entry.getKey()));

					switch (superType) {
					case TYPE_BOOLEAN:
						// undefined which type
						NumberRecord nr = new NumberRecord();
						nr.setIndex(null);
						nr.setIsElementInArray(false);
						nr.setParentField(parent);
						nr.setRecordName(entry.getKey());
						nr.setRequest(req);
						nr.setServiceRelated(req.getServiceRelated());
						nr.setValue(null);
						nr.setWholeNumber(null);
						StringRecord sr = new StringRecord();
						sr.setIndex(null);
						sr.setIsElementInArray(false);
						sr.setParentField(parent);
						sr.setRecordName(entry.getKey());
						sr.setRequest(req);
						sr.setServiceRelated(req.getServiceRelated());
						sr.setLength(null);
						sr.setPattern(null);
						sr.setValue(null);

						this.save(nr);
						this.save(sr);
						break;
					case TYPE_BOOLEAN_ARRAY:
						// undefined
						ObjectRecord or = new ObjectRecord();
						or.setIndex(indx);
						or.setIsArray(true);
						or.setIsElementInArray(indx != null);
						or.setParentField(parent);
						or.setRecordName(entry.getKey());
						or.setRequest(req);
						or.setServiceRelated(req.getServiceRelated());
						this.save(or);

						NumberRecord numr = new NumberRecord();
						numr.setIndex(0);
						numr.setIsElementInArray(true);
						numr.setParentField(or);
						numr.setRecordName(entry.getKey() + "_numeric_value");
						numr.setRequest(req);
						numr.setServiceRelated(req.getServiceRelated());
						numr.setValue(null);
						numr.setWholeNumber(null);
						StringRecord strr = new StringRecord();
						strr.setIndex(0);
						strr.setIsElementInArray(true);
						strr.setParentField(or);
						strr.setRecordName(entry.getKey() + "_string_value");
						strr.setRequest(req);
						strr.setServiceRelated(req.getServiceRelated());
						strr.setLength(null);
						strr.setPattern(null);
						strr.setValue(null);

						this.save(numr);
						this.save(strr);

						break;
					case TYPE_ENUM:
						this.saveStringRecord(entry.getKey(), parent, null, null, req);

						break;
					case TYPE_ENUM_ARRAY:
						ObjectRecord arrRecE = this.saveArrayRecord(entry.getKey(), parent, req, indx);
						this.saveStringRecord(entry.getKey() + "_string_value", arrRecE, null, 0, req);
						break;
					case TYPE_NUMBER:
						this.saveNumberRecord(entry.getKey(), parent, null, null, req);
						break;
					case TYPE_NUMBER_ARRAY:
						ObjectRecord arrRecN = this.saveArrayRecord(entry.getKey(), parent, req, indx);

						this.saveNumberRecord(entry.getKey() + "_numeric_value", arrRecN, null, 0, req);
						break;
					case TYPE_OBJECT:
						this.saveObjectRecord(entry.getKey(), parent, req, indx);
						break;
					case TYPE_OBJECT_ARRAY:
						ObjectRecord arrRec = this.saveArrayRecord(entry.getKey(), parent, req, indx);
						this.saveObjectRecord(
								entry.getKey() + (parent instanceof ObjectRecord && ((ObjectRecord) parent).getIsArray()
										? "_array_value"
										: ""),
								arrRec, req, 0);
						break;
					case TYPE_STRING:
						this.saveStringRecord(entry.getKey(), parent, (String) entry.getValue(), null, req);
						break;
					case TYPE_STRING_ARRAY:
						ObjectRecord arrRecS = this.saveArrayRecord(entry.getKey(), parent, req, indx);
						this.saveStringRecord(entry.getKey() + "_string_value", arrRecS, null, 0, req);
						break;
					default:
						break;
					}
				}
				break;
			case TYPE_EMPTY_ARRAY:
				ObjectRecord r = new ObjectRecord();
				r.setIsArray(true);
				r.setIsElementInArray(indx != null);
				r.setIndex(null);
				r.setParentField(parent);
				r.setRecordName(entry.getKey());
				r.setRequest(req);
				r.setServiceRelated(req.getServiceRelated());
				this.save(r);
				break;
			case TYPE_NUMBER:
				this.saveNumberRecord(entry.getKey(), parent, (Number) entry.getValue(), null, req);
				break;
			case TYPE_NUMBER_ARRAY:
				ObjectRecord arrRecN = this.saveArrayRecord(entry.getKey(), parent, req, indx);
				List<?> listN = (List<?>) entry.getValue();
				for (int i = 0; i < listN.size(); i++) {
					this.saveNumberRecord(entry.getKey() + "_array_value", arrRecN, (Number) listN.get(i), i, req);
				}
				break;
			case TYPE_OBJECT:
				ObjectRecord obr = this.saveObjectRecord(entry.getKey(), parent, req, indx);
				Field pto = expectedClass == null ? null
						: (Field) MappingUtils.remapFields(expectedClass.getFields()).get(entry.getKey());
				this.bulkSaveRecords((Map<String, Object>) entry.getValue(), req, obr,
						pto != null ? pto.getType() : null);
				break;
			case TYPE_OBJECT_ARRAY:
				ObjectRecord arrRec = this.saveArrayRecord(entry.getKey(), parent, req, indx);
				List<?> listObj = (List<?>) entry.getValue();
				Field pt = expectedClass == null ? null
						: (Field) MappingUtils.remapFields(expectedClass.getFields()).get(entry.getKey());
				for (int i = 0; i < listObj.size(); i++) {
					if (listObj.get(i) instanceof List) {
						this.processArrayRecord(
								entry.getKey() + (parent instanceof ObjectRecord && ((ObjectRecord) parent).getIsArray()
										? "_embedded"
										: ""),
								(List<Object>) listObj.get(i), req, arrRec,
								pt == null ? null
										: (ParameterizedType) ((ParameterizedType) pt.getGenericType())
												.getActualTypeArguments()[0],
								i, 0);
					} else {
						this.bulkSaveRecords((Map<String, Object>) listObj.get(i), req, arrRec, pt == null ? null
								: (Class<?>) ((ParameterizedType) pt.getGenericType()).getActualTypeArguments()[0]);
					}
				}
				break;
			case TYPE_STRING:
				this.saveStringRecord(entry.getKey(), parent, (String) entry.getValue(), null, req);
				break;
			case TYPE_STRING_ARRAY:
				ObjectRecord arrRecS = this.saveArrayRecord(entry.getKey(), parent, req, indx);
				List<?> listS = (List<?>) entry.getValue();
				for (int i = 0; i < listS.size(); i++) {
					this.saveStringRecord(entry.getKey() + "_array_value", arrRecS, (String) listS.get(i), i, req);
				}
				break;
			default:
				break;

			}

		}

		return true;
	}

	public ObjectRecord processArrayRecord(String pn, List<Object> map, Request req,
			com.kmarinov.serialize.entities.Record parent, ParameterizedType expectedClass, Integer indx,
			Integer depth) {
		ObjectRecord or = this.saveArrayRecord(pn, parent, req, indx);
		for (int i = 0; i < map.size(); i++) {
			if (map.get(i) instanceof List) {
				this.processArrayRecord(pn + "_embedded", (List<Object>) map.get(i), req, or,
						expectedClass != null ? (ParameterizedType) expectedClass.getActualTypeArguments()[0] : null, i,
						depth + 1);
			} else if (map.get(i) instanceof Map) {
				this.bulkSaveRecords((Map<String, Object>) map.get(i), req, or,
						expectedClass != null ? (Class<?>) expectedClass.getActualTypeArguments()[0] : null, indx);
			} else if (map.get(i) instanceof Number) {
				this.saveNumberRecord(pn + "_array_value", parent, (Number) map.get(i), indx, req);
			} else {
				this.saveStringRecord(pn + "_array_value", parent, (String) map.get(i), indx, req);
			}
		}
		return or;
	}

}
