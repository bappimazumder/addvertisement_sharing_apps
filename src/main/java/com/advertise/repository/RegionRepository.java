package com.advertise.repository;

import com.advertise.entity.lookup.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region,Long> {
    Region findByName(String cityName);
}
