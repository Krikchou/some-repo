package com.kmarinov.serialize.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECORD")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("record")
public class Record {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RECORD_ID")
	private Long id;
	@Column(name = "RECORD_NAME")
	private String recordName;
	@Column(name = "SERVICE_RELATED")
	private String serviceRelated;
	@Column(name = "INDEX")
	private Integer index;
	@Column(name = "ELEMENT_IN_ARRAY")
	private Boolean isElementInArray;
	
	@ManyToOne
	@JoinColumn(name = "PARENT_FIELD", referencedColumnName = "RECORD_ID")
	private Record parentField;
	
	@ManyToOne
	@JoinColumn(name = "REQUEST_ID", referencedColumnName = "REQUEST_ID")
	private Request request;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getServiceRelated() {
		return serviceRelated;
	}

	public void setServiceRelated(String serviceRelated) {
		this.serviceRelated = serviceRelated;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Boolean getIsElementInArray() {
		return isElementInArray;
	}

	public void setIsElementInArray(Boolean isElementInArray) {
		this.isElementInArray = isElementInArray;
	}

	public Record getParentField() {
		return parentField;
	}

	public void setParentField(Record parentField) {
		this.parentField = parentField;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}
