package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Activity;
import com.advertise.request.ad.ActivityRequestDto;
import com.advertise.response.ad.ActivityResponseDto;
import com.advertise.serviceImpl.advertisement.ActivityServiceImpl;
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
@SpringBootTest(classes = {ActivityControllerMockitoTests.class})
public class ActivityControllerMockitoTests {

    @Mock
    ActivityServiceImpl service;

    @InjectMocks
    ActivityController controller;

    @Mock
    private Page<Activity> activityPage;

    @Mock
    private Pageable pageableMock;

    @Test
    @Order(1)
    public void test_addActivity() throws AdvartiseException {
        Activity activity = new Activity();
        activity.setName("Activity Name");
        ActivityRequestDto requestDto = new ActivityRequestDto();
        requestDto.setName(activity.getName());

        ActivityResponseDto responseDto = new ActivityResponseDto();
        responseDto.setId(activity.getId());
        responseDto.setName(activity.getName());

        when(service.save(activity)).thenReturn(activity);
        ResponseEntity response = controller.addActivity(requestDto);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(responseDto, response.getBody());
    }

    @Test
    @Order(2)
    public void test_updateActivity() throws AdvartiseException {
        Activity activity = new Activity(1L, "Activity Name");
        Long activityId = 1L;
        ActivityRequestDto requestDto = new ActivityRequestDto();
        requestDto.setName("Activity Name Updated ");

        when(service.findById(activityId)).thenReturn(activity);
        when(service.update(activity)).thenReturn(activity);
        ResponseEntity response = controller.updateActivity(activityId, requestDto);
        Activity saved = (Activity) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(activityId, saved.getId());
        assertEquals(requestDto.getName(), saved.getName());
    }

    @Test
    @Order(3)
    public void test_deleteActivity() throws AdvartiseException {
        Activity activity = new Activity(1L, "Activity Name");
        Long activityId = 1L;
        when(service.findById(activityId)).thenReturn(activity);
        ResponseEntity response = controller.deleteActivityById(activityId);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(4)
    public void test_getAllActivity() {
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(1L, "Activity name 1"));
        activityList.add(new Activity(2L, "Activity name 2"));
        activityPage = new PageImpl(activityList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));
        when(service.getAll(pageableMock)).thenReturn(activityPage);
        assertEquals(controller.getAllActivity(pageableMock).getSize(), 2);
    }
}
