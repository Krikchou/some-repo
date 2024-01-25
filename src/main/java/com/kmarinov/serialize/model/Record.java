package com.kmarinov.serialize.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Record {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RECORD_ID")
	private Long id;
	@Column(name = "RECORD_NAME")
	private String recordName;
	@Column(name = "SERVICE_RELATED")
	private String serviceRelated;

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
}
