package com.advertise.service.advertisement;

import com.advertise.entity.advertisement.AgeGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgeGroupService {
    AgeGroup findById(Long id);
    AgeGroup findByValue(String value);
    AgeGroup save(AgeGroup entity);
    AgeGroup update(AgeGroup entity);
    void delete(AgeGroup entity);
    Page<AgeGroup> getAll(Pageable pageable);

}
