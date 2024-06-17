package com.kmarinov.serialize.entities;

import java.util.HashSet;
import java.util.Set;

import com.kmarinov.serialize.enums.RequestStateEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="REQUEST_DATA")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REQUEST_ID")
	private Long id;
	
	@Column(name = "BODY", columnDefinition="TEXT")
	private String requestBody;
	
	@Column(name = "SERVICE_RELATED")
	private String serviceRelated;
	
	@Column(name = "API_PATH")
	private String apiPath;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATE")
	private RequestStateEnum state;
	
	@OneToMany(mappedBy = "request")
	private Set<com.kmarinov.serialize.entities.Record> records;
	
	public Request() {
		records = new HashSet<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getServiceRelated() {
		return serviceRelated;
	}

	public void setServiceRelated(String serviceRelated) {
		this.serviceRelated = serviceRelated;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public RequestStateEnum getState() {
		return state;
	}

	public void setState(RequestStateEnum state) {
		this.state = state;
	}

	public Set<Record> getRecords() {
		return records;
	}

	public void setRecords(Set<Record> records) {
		this.records = records;
	}
	
	
	
}
