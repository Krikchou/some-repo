package com.kmarinov.serialize.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kmarinov.serialize.entities.ObjectRecord;

@Repository
public interface ObjectRecordRepository extends JpaRepository<ObjectRecord, Long> {
	List<ObjectRecord> findByServiceRelatedOrderByRecordNameAsc(String serviceRelated);
	List<ObjectRecord> findByServiceRelatedAndIsArrayTrueOrderByRecordNameAsc(String serviceRelated);
	List<ObjectRecord> findByServiceRelatedAndIsArrayFalseOrderByRecordNameAsc(String serviceRelated);
}
