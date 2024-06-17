package com.kmarinov.serialize.api;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DummyInputBean {
	String stringProperty;
	Double doubleProperty;
	Integer integerProperty;
	DummyEnum enumProperty;
	List<String> stringListProperty;
	List<Double> doubleListProperty;
	List<Integer> integerListProperty;
	List<InnerDummyInputBean> embeddedObject;
	List<List<Integer>> doublyEmbeddedInteger;
	List<List<List<Integer>>> triplyEmbeddedInteger;
}
