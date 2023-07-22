package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.request.CountryRequestDto;
import com.advertise.request.RegionRequestDto;
import com.advertise.response.lookup.CountryResponseDto;
import com.advertise.service.lookup.LookupService;
import com.advertise.util.Util;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {LookupControllerMockitoTests.class})
class LookupControllerMockitoTests {

    @InjectMocks
    LookupController lookupController;

    @Mock
    LookupService lookupService;

    @Mock
    private Page<Country> countryPage;

    @Mock
    private Page<Region> regionPage;

    @Mock
    private Pageable pageableMock;

    @Test
    @Order(1)
    public void test_addCountry() throws AdvartiseException {
        Country country = new Country();
        country.setName("Test Mock");
        country.setCountryCode("code");

        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setName(country.getName());
        requestDto.setCountryCode(country.getCountryCode());

        CountryResponseDto responseDto = new CountryResponseDto();
        responseDto.setId(country.getId());
        responseDto.setName(country.getName());
        responseDto.setCountryCode(country.getCountryCode());

        when(lookupService.saveCountry(requestDto)).thenReturn(responseDto);
        ResponseEntity response = lookupController.addCountry(requestDto);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(responseDto, response.getBody());
    }

    @Test
    @Order(2)
    public void test_updateCountry() throws Exception {
        Country country = new Country(1L, "Afghanistan","AF");
        long countryId = country.getId();
        CountryRequestDto requestDto = new CountryRequestDto();
        requestDto.setCountryCode("AF2");
        requestDto.setName("test Af");

        CountryResponseDto responseDto = new CountryResponseDto();

        when(lookupService.getCountryById(country.getId())).thenReturn(responseDto);
        when(lookupService.updateCountry(country.getId(),requestDto)).thenReturn(responseDto);
        ResponseEntity<Object> response = lookupController.updateCountry(countryId, requestDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    @Order(3)
    public void test_deleteCountry() throws Exception {
        Country country = new Country(1L,"Afghanistan","AF");
        CountryResponseDto responseDto = Util.convertClass(country,CountryResponseDto.class);

        when(lookupService.getCountryById(responseDto.getId())).thenReturn(responseDto);
        ResponseEntity response = lookupController.deleteCountryById(responseDto.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(4)
    public void test_addRegion() throws Exception {
        Region region = new Region();
        region.setName("Test Mock");
        region.setCode("code");

        RegionRequestDto responseDto = new RegionRequestDto();
        responseDto.setName(region.getName());
        responseDto.setCode(region.getCode());

        when(lookupService.saveRegion(region)).thenReturn(region);
        ResponseEntity response = lookupController.addRegion(responseDto);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(region, response.getBody());
    }

    @Test
    @Order(5)
    public void test_updateRegion() throws Exception {
        Region region = new Region(1L,"sff","wfewf");
        RegionRequestDto req= Util.convertClass(region,RegionRequestDto.class);
        when(lookupService.getRegionById(region.getId())).thenReturn(region);
        when(lookupService.updateRegion(region)).thenReturn(region);
        ResponseEntity<Object> response = lookupController.updateRegion(region.getId(), req);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(6)
    public void test_deleteRegion() throws Exception {
        Region region = new Region(1L,"testR","tR");
        when(lookupService.getRegionById(region.getId())).thenReturn(region);
        ResponseEntity response = lookupController.deleteRegionById(region.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(7)
    public void test_getAllCountry() {
        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1L, "Franch","description"));
        countryList.add(new Country(2L, "Bangladesh",""));
        countryPage = new PageImpl(countryList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));
        when(lookupService.getAllCountry(pageableMock)).thenReturn(countryPage);
        assertEquals(lookupController.getAllCountry(pageableMock).getSize(), 2);
    }

    @Test
    @Order(8)
    public void test_getAllRegion() {
        List<Region> regionsList = new ArrayList<>();
        regionsList.add(new Region(1L, "Dhaka",null));
        regionsList.add(new Region(2L, "Paris",null));
        regionPage = new PageImpl(regionsList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));
        when(lookupService.getAllRegion(pageableMock)).thenReturn(regionPage);
        assertEquals(lookupController.getAllRegion(pageableMock).getSize(), 2);
    }
}