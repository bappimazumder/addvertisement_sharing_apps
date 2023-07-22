package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.request.ad.AgeGroupRequestDto;
import com.advertise.response.ad.AgeGroupResponseDto;
import com.advertise.serviceImpl.advertisement.AgeGroupServiceImpl;
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
@SpringBootTest(classes = {AgeGroupControllerMockitoTests.class})
public class AgeGroupControllerMockitoTests {

    @Mock
    AgeGroupServiceImpl service;

    @InjectMocks
    AgeGroupController controller;

    @Mock
    private Page<AgeGroup> ageGroupPage;

    @Mock
    private Pageable pageableMock;

    @Test
    @Order(1)
    public void test_addAgeGroup() throws AdvartiseException {
        AgeGroup ageGroup = new AgeGroup();
        ageGroup.setValue("AgeGroup value");
        AgeGroupRequestDto requestDto = new AgeGroupRequestDto();
        requestDto.setValue(ageGroup.getValue());

        AgeGroupResponseDto responseDto = new AgeGroupResponseDto();
        responseDto.setId(ageGroup.getId());
        responseDto.setValue(ageGroup.getValue());

        when(service.save(ageGroup)).thenReturn(ageGroup);
        ResponseEntity response = controller.addAgeGroup(requestDto);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(responseDto, response.getBody());
    }

    @Test
    @Order(2)
    public void test_updateAgeGroup() throws AdvartiseException {
        AgeGroup ageGroup = new AgeGroup(1L, "AgeGroup Value");
        Long ageGroupId = 1L;
        AgeGroupRequestDto requestDto = new AgeGroupRequestDto();
        requestDto.setValue("AgeGroup Value Updated");

        when(service.findById(ageGroupId)).thenReturn(ageGroup);
        when(service.update(ageGroup)).thenReturn(ageGroup);
        ResponseEntity response = controller.updateAgeGroup(ageGroupId, requestDto);
        AgeGroup saved = (AgeGroup) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(ageGroupId, saved.getId());
        assertEquals(requestDto.getValue(), saved.getValue());
    }

    @Test
    @Order(3)
    public void test_deleteAgeGroup() throws AdvartiseException {
        AgeGroup ageGroup = new AgeGroup(1L, "AgeGroup Name");
        Long ageGroupId = 1L;
        when(service.findById(ageGroupId)).thenReturn(ageGroup);
        ResponseEntity response = controller.deleteAgeGroupById(ageGroupId);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(4)
    public void test_getAllAgeGroup() {
        List<AgeGroup> ageGroupList = new ArrayList<>();
        ageGroupList.add(new AgeGroup(1L, "AgeGroup name 1"));
        ageGroupList.add(new AgeGroup(2L, "AgeGroup name 2"));
        ageGroupPage = new PageImpl(ageGroupList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));
        when(service.getAll(pageableMock)).thenReturn(ageGroupPage);
        assertEquals(controller.getAllAgeGroup(pageableMock).getSize(), 2);
    }
}
