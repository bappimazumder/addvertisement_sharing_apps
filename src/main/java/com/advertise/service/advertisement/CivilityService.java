package com.advertise.service.advertisement;


import com.advertise.entity.advertisement.Civility;

public interface CivilityService {

    Civility findById(Long id);
    Civility findByValue(String value);
}
