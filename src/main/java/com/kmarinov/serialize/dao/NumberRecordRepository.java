package com.kmarinov.serialize.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kmarinov.serialize.entities.NumberRecord;

@Repository
public interface NumberRecordRepository extends JpaRepository<NumberRecord, Long> {
	List<NumberRecord> findByServiceRelatedAndRecordNameOrderByValueAsc(String serviceRelated, String recordName);
	
	@Query("select distinct nr.recordName from NumberRecord nr where nr.serviceRelated = ?1")
	List<String> findDistinctRecordsForService(String serviceRelated);
	
	@Query("select distinct nr.value from NumberRecord nr where nr.serviceRelated = ?1 and nr.recordName = ?2 order by nr.value ASC")
	List<BigDecimal> listDistinctValues(String serviceRelated, String recordName);
}
