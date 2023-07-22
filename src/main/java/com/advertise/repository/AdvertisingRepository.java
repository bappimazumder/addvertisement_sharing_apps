package com.advertise.repository;

import com.advertise.entity.advertisement.Advertising;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisingRepository  extends JpaRepository<Advertising, Long> {
    Advertising findAdvertisingByReference(String reference);
}
