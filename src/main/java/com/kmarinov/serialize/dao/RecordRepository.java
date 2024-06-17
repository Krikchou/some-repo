package com.kmarinov.serialize.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository  extends JpaRepository<com.kmarinov.serialize.entities.Record, Long> {
	List<com.kmarinov.serialize.entities.Record> findAllByRequestServiceRelated(String requestServiceRelated);
	List<com.kmarinov.serialize.entities.Record> findAllByRecordNameAndRequestServiceRelated(String recordName, String request);
	@Query("select distinct r.recordName from Record r where r.serviceRelated = ?1")
	List<String> findDistinctRecordsForService(String serviceRelated);
}
