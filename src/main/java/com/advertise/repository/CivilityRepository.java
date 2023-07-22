package com.advertise.repository;

import com.advertise.entity.advertisement.Civility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CivilityRepository extends JpaRepository<Civility, Long> {

    Civility findByValue(String value);
}
