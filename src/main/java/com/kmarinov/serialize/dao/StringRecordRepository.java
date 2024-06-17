package com.kmarinov.serialize.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kmarinov.serialize.entities.StringRecord;

@Repository
public interface StringRecordRepository extends JpaRepository<StringRecord, Long> {
	List<StringRecord> findByServiceRelatedOrderByValueAsc(String serviceRelated);
	List<StringRecord> findByServiceRelatedAndRecordNameOrderByValueAsc(String serviceRelated, String recordName);
	@Query("select distinct sr.recordName from StringRecord sr where sr.serviceRelated = ?1")
	List<String> findDistinctRecordsForService(String serviceRelated);
}
