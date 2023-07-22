package com.advertise.serviceImpl.advertisement;

import com.advertise.entity.advertisement.Civility;
import com.advertise.repository.CivilityRepository;
import com.advertise.service.advertisement.CivilityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CivilityServiceImpl implements CivilityService {
    @Autowired
    CivilityRepository repository;

    @Override
    public Civility findById(Long id) {
        Optional<Civility> civilityOptional = repository.findById(id);
        Civility civility = null;
        if(civilityOptional.isPresent()){
            civility = civilityOptional.get();
        }
        return civility;
    }

    @Override
    public Civility findByValue(String value) {
        Civility civility =  repository.findByValue(value);
        return civility;
    }
}
