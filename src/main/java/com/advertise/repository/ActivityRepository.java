package com.advertise.repository;

import com.advertise.entity.advertisement.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findActivityById(Long id);
    Activity findByName(String name);
}
