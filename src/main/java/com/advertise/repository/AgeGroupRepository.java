package com.advertise.repository;

import com.advertise.entity.advertisement.AgeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgeGroupRepository extends JpaRepository<AgeGroup, Long> {

    AgeGroup findByValue(String value);
}
