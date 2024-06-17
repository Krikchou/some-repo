package com.kmarinov.serialize.entities;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("objectRecord")
public class ObjectRecord extends Record {
	@Column(name = "IS_ARRAY")
	private Boolean isArray;
	@OneToMany(mappedBy = "parentField")
	private Set<Record> containedRecords;
	public Boolean getIsArray() {
		return isArray;
	}
	public void setIsArray(Boolean isArray) {
		this.isArray = isArray;
	}
	public Set<Record> getContainedRecords() {
		return containedRecords;
	}
	public void setContainedRecords(Set<Record> containedRecords) {
		this.containedRecords = containedRecords;
	}
	
}
