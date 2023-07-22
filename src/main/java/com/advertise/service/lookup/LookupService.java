package com.advertise.service.lookup;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.request.CountryRequestDto;
import com.advertise.response.lookup.CountryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface LookupService {
    CountryResponseDto saveCountry(CountryRequestDto countryRequestBean) throws AdvartiseException;
    CountryResponseDto getCountryById(Long countryId) throws Exception;
    List<CountryResponseDto> getAllCountryList() throws Exception;
    CountryResponseDto updateCountry(Long id,CountryRequestDto countryRequestDto) throws Exception;

    Country getCountryByName(String countryName);

    Boolean deleteCountryById(Long countryId) throws Exception;

    Region saveRegion(Region region) throws Exception;
    Region updateRegion(Region region) throws Exception;

    Region getRegionById(Long id) throws Exception;

    Boolean deleteRegionById(Long id) throws Exception;

    Page<Region> getAllRegion(Pageable pageable);
    Page<Country> getAllCountry(Pageable pageable);
    Region getRegionByName(String regionName);

}
