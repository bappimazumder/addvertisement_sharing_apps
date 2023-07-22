package com.advertise.service.advertisement;

import com.advertise.entity.advertisement.Advertising;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdvertisingService {
    Advertising findById(Long id);
    Advertising findByReference(String reference);
    Advertising save(Advertising entity);
    Advertising update(Advertising entity);
    void delete(Advertising entity);
    Page<Advertising> getAll(Pageable pageable);
    List<Advertising> findAll();
}
