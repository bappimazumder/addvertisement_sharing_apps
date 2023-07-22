package com.advertise.service.advertisement;

import com.advertise.entity.advertisement.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityService {
    Activity findById(Long id);
    Activity save(Activity entity);
    Activity update(Activity entity);
    void delete(Activity entity);
    Page<Activity> getAll(Pageable pageable);
    Activity findActivityById(Long id);
    Activity findByName(String name);
}
