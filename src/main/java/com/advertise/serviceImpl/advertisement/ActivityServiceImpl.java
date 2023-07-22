package com.advertise.serviceImpl.advertisement;

import com.advertise.entity.advertisement.Activity;
import com.advertise.repository.ActivityRepository;
import com.advertise.service.advertisement.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    ActivityRepository repository;

    @Override
    public Activity findById(Long id) {
        Optional<Activity> activityOptional = repository.findById(id);
        Activity activity = null;
        if(activityOptional.isPresent()){
            activity = activityOptional.get();
        }
        return activity;
    }

    @Override
    public Activity save(Activity entity) {
        return repository.save(entity);
    }

    @Override
    public Activity update(Activity entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Activity entity) {
        repository.delete(entity);
    }

    @Override
    public Page<Activity> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Activity findActivityById(Long id) {
        return repository.findActivityById(id);
    }

    @Override
    public Activity findByName(String name) {
        Activity model =  repository.findByName(name);
        return model;
    }

}
