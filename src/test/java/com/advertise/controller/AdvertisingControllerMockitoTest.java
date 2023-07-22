package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Advertising;
import com.advertise.entity.user.UserDetails;
import com.advertise.request.ad.AdvertisingRequestDto;
import com.advertise.request.ad.TargetingRequestDto;
import com.advertise.response.ad.AdvertisingResponseDto;
import com.advertise.service.advertisement.AgeGroupService;
import com.advertise.serviceImpl.UserServiceImpl;
import com.advertise.serviceImpl.advertisement.ActivityServiceImpl;
import com.advertise.serviceImpl.advertisement.AdvertisingServiceImpl;
import com.advertise.serviceImpl.advertisement.AgeGroupServiceImpl;
import com.advertise.serviceImpl.lookup.LookupServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {AdvertisingControllerMockitoTest.class})
public class AdvertisingControllerMockitoTest {

    @Mock
    AdvertisingServiceImpl service;

    @Mock
    UserServiceImpl userService;

    @InjectMocks
    AdvertisingController controller;

    @Mock
    private Page<Advertising> advertisingPage;

    @Mock
    private Pageable pageableMock;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    LookupServiceImpl lookupService;
    @Mock
    AgeGroupServiceImpl ageGroupService;

    @Mock
    ActivityServiceImpl activityService;
    private LocalDateTime localDate = LocalDateTime.now();
    private BigDecimal budget = new BigDecimal("100");
    private BigDecimal rate = new BigDecimal("0.03");
    private String reference = "R-0001";

    private String targeting = "13-14 ans";

    private String status = "finished";
    private Boolean isHeaderLocation = false;
    private  String  productLink= "https://www.google.com/";

    private String title = "Advertisement Title";
    private  String companyDescription = "Company Description";
    private  String samplePostTitle = "Sample post title";
    private  String samplePostDescription = "Sample post description";
    private byte[] data = new byte[]{1, 2, 3, 4};

    @Test
    @Order(1)
    public void test_addAdvertising() throws AdvartiseException, IOException {
        String anyString = "{\"country\":[\"France\"],\"region\":[\"Alsace\"],\"ageGroup\":[\"13-17 ans\"],\"activity\":[\"Alimentation\"],\"gender\":[\"Men\"]}";
        final MultipartFile primaryFile = mock(MultipartFile.class);
        when(primaryFile.getContentType()).thenReturn(ContentType.IMAGE_JPEG.getMimeType());
        when(primaryFile.getSize()).thenReturn(3024L);
        when(primaryFile.getSize()).thenReturn(3024L);

        final MultipartFile secondaryFile = mock(MultipartFile.class);
        when(secondaryFile.getContentType()).thenReturn(ContentType.IMAGE_JPEG.getMimeType());
        when(secondaryFile.getSize()).thenReturn(3024L);
        when(secondaryFile.getSize()).thenReturn(3024L);

        final MultipartFile sampleFile = mock(MultipartFile.class);
        when(sampleFile.getContentType()).thenReturn(ContentType.IMAGE_JPEG.getMimeType());
        when(sampleFile.getSize()).thenReturn(3024L);
        when(sampleFile.getSize()).thenReturn(3024L);

        Advertising advertising = new Advertising(1L, "R-0001", localDate, localDate, budget, rate, false);
        advertising.setPrimaryFile(primaryFile);
        advertising.setSecondaryFile(secondaryFile);
        advertising.setTargeting(anyString);

        Advertising existingAdvertising = new Advertising(1L, "R-0000", localDate, localDate, budget, rate, false);
        when(service.findByReference("R-0001")).thenReturn(null);
        when(service.save(advertising)).thenReturn(advertising);

        AdvertisingResponseDto dto = new AdvertisingResponseDto();
        dto.setId(1);
        dto.setReference(reference);
        advertising.setIsDeleted(true);
        TargetingRequestDto targetingRequestDto = new TargetingRequestDto();
        String country = "France";
        List<String> countries = new ArrayList<>();
        countries.add(country);
        targetingRequestDto.setCountry(countries);
        when(objectMapper.readValue(anyString, TargetingRequestDto.class)).thenReturn(targetingRequestDto);
        when(lookupService.getCountryByName("name")).thenReturn(null);
        when(lookupService.getRegionByName("name")).thenReturn(null);
        when(ageGroupService.findByValue("name")).thenReturn(null);
        when(activityService.findByName("name")).thenReturn(null);

        ResponseEntity response = controller.saveAdd(reference, localDate, localDate, budget, rate,
                isHeaderLocation, anyString,primaryFile,secondaryFile,status,productLink,title,companyDescription,samplePostTitle,samplePostDescription,sampleFile);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        //  assertEquals(responseDto,response.getBody());
    }

    @Test
    @Order(2)
    public void test_updateAdvertising() throws AdvartiseException {
        Advertising advertising = new Advertising(1L, "R-0001", localDate, localDate, budget, rate, false);
        String advertisingReference = "R-0001";
        AdvertisingRequestDto requestDto = new AdvertisingRequestDto();
        requestDto.setTargeting(new TargetingRequestDto());
        requestDto.setStatus("Finished");
        when(service.findByReference(advertisingReference)).thenReturn(advertising);
        when(service.findByReference(reference)).thenReturn(advertising);
        when(service.update(advertising)).thenReturn(advertising);
        ResponseEntity response = controller.updateAdvertisement(advertisingReference, requestDto);
        AdvertisingResponseDto dto = (AdvertisingResponseDto) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(advertisingReference, dto.getReference());
        //assertEquals(requestDto.getReference(), dto.getReference());
    }

    @Test
    @Order(3)
    public void test_deleteAdvertising() throws AdvartiseException {
        Advertising advertising = new Advertising(1L, "R-0001", localDate, localDate, budget, rate, false);
        Long advertisingId = 1L;
        when(service.findByReference(reference)).thenReturn(advertising);
        ResponseEntity response = controller.deleteAddByReference(reference);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(4)
    public void test_getAllAdvertising() throws Exception {
        String userName = "test@gmail.com";
        UserDetails userDetails = new UserDetails();
        List<Advertising> advertisingList = new ArrayList<>();
        advertisingList.add(new Advertising(1L, "R-0001", localDate, localDate, budget, rate, false));
        advertisingList.add(new Advertising(2L, "R-0002", localDate, localDate, budget, rate, false));
        advertisingPage = new PageImpl(advertisingList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));
        when(userService.findByUsername(userName)).thenReturn(userDetails);
        when(service.getAll(pageableMock)).thenReturn(advertisingPage);
        assertEquals(controller.getAllAdvertisements(pageableMock).getSize(), 0);
    }

}
