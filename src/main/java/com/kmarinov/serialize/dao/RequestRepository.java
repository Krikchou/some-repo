package com.kmarinov.serialize.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kmarinov.serialize.entities.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

}
