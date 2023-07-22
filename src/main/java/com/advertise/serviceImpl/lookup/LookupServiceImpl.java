package com.advertise.serviceImpl.lookup;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.repository.CountryRepository;
import com.advertise.repository.RegionRepository;
import com.advertise.request.CountryRequestDto;
import com.advertise.response.lookup.CountryResponseDto;
import com.advertise.service.lookup.LookupService;
import com.advertise.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LookupServiceImpl implements LookupService {
   CountryRepository countryRepository;
   RegionRepository regionRepository;

    @Autowired
    public LookupServiceImpl(CountryRepository lookupRepository,RegionRepository regionRepository){
        this.countryRepository = lookupRepository;
        this.regionRepository=regionRepository;
    }
    private Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

    @Override
    public CountryResponseDto saveCountry(CountryRequestDto countryRequestBean) throws AdvartiseException {
        CountryResponseDto responseDto=null;
            Country country = Util.convertClass(countryRequestBean, Country.class);
            responseDto= Util.convertClass(countryRepository.save(country), CountryResponseDto.class);
       return responseDto;
    }

    @Override
    public CountryResponseDto getCountryById(Long countryId) throws Exception {
        Country country =null;
        CountryResponseDto countryResponseDto =null;
        Optional<Country> countryObject = countryRepository.findById(countryId);

        if(countryObject.isPresent())
            country=countryObject.get();

        if(country !=null)
            countryResponseDto= Util.convertClass(country, CountryResponseDto.class);

        return countryResponseDto;
    }

    @Override
    public List<CountryResponseDto> getAllCountryList() throws Exception {
        List<CountryResponseDto> countryResponseDtoList =null;
        List<Country> countryList= countryRepository.findAll();

        if(countryList.size()>0)
            countryResponseDtoList= Util.toDtoList(countryList,CountryResponseDto.class);

        return countryResponseDtoList;
    }

    @Override
    public CountryResponseDto updateCountry(Long id,CountryRequestDto countryRequestDto) throws Exception {
        CountryResponseDto countryResponseDto=null;

            Country country =countryRepository.findById(id).get();
            country.setName(countryRequestDto.getName());
            country.setCountryCode(countryRequestDto.getCountryCode());
            countryResponseDto=Util.convertClass(countryRepository.save(country), CountryResponseDto.class);
        return countryResponseDto;
    }

    @Override
    public Country getCountryByName(String countryName) {
      Country country =  countryRepository.findByName(countryName);
        return country;
    }

    @Override
    public Boolean deleteCountryById(Long countryId) throws Exception {
        countryRepository.deleteById(countryId);
        logger.info(countryId+" is deleted.");
        return true;
    }

    @Override
    public Region saveRegion(Region region) throws Exception {
       return regionRepository.save(region);
    }

    @Override
    public Region updateRegion(Region region) throws Exception {
        return regionRepository.save(region);
    }

    @Override
    public Region getRegionById(Long id) throws Exception {
        Region region=null;
        Optional<Region> regionOp = regionRepository.findById(id);

        if(regionOp.isPresent())
            region=regionOp.get();

        return region;
    }

    @Override
    public Boolean deleteRegionById(Long regionId) throws Exception {
        regionRepository.deleteById(regionId);
        logger.info(regionId+" is deleted.");
        return true;
    }

    @Override
    public Region getRegionByName(String regionName) {
        Region region =  regionRepository.findByName(regionName);
        return region;
    }

    @Override
    public Page<Region> getAllRegion(Pageable pageable) {
        return regionRepository.findAll(pageable);
    }

    @Override
    public Page<Country> getAllCountry(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }
}
