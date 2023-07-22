package com.advertise.serviceImpl.advertisement;

import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.repository.AgeGroupRepository;
import com.advertise.service.advertisement.AgeGroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AgeGroupServiceImpl implements AgeGroupService{

    AgeGroupRepository repository;

    @Override
    public AgeGroup findById(Long id) {
        Optional<AgeGroup> ageGroupOptional = repository.findById(id);
        AgeGroup ageGroup = null;
        if(ageGroupOptional.isPresent()){
            ageGroup = ageGroupOptional.get();
        }
        return ageGroup;
    }

    @Override
    public AgeGroup findByValue(String value) {
        AgeGroup ageGroup =  repository.findByValue(value);
        return ageGroup;
    }

    @Override
    public AgeGroup save(AgeGroup entity) {
        return repository.save(entity);
    }

    @Override
    public AgeGroup update(AgeGroup entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(AgeGroup entity) {
        repository.delete(entity);
    }

    @Override
    public Page<AgeGroup> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
