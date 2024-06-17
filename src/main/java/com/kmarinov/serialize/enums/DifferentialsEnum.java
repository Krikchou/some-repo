package com.kmarinov.serialize.enums;

public enum DifferentialsEnum {
	TYPE_NUMBER_ARRAY,
	TYPE_STRING_ARRAY,
	TYPE_OBJECT_ARRAY,
	TYPE_EMPTY_ARRAY,
	TYPE_BOOLEAN_ARRAY,
	TYPE_ENUM_ARRAY,
	TYPE_ARRAY_NO_TYPE,
	TYPE_OBJECT,
	TYPE_NUMBER,
	TYPE_STRING,
	TYPE_BOOLEAN, // sanity checks
	TYPE_ENUM, // sanity checks
	MISSING, // missing from tree
	NULL // present, but null
}
