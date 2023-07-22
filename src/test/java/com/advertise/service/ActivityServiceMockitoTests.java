package com.advertise.service;

import com.advertise.entity.advertisement.Activity;
import com.advertise.repository.ActivityRepository;
import com.advertise.serviceImpl.advertisement.ActivityServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ActivityServiceMockitoTests.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ActivityServiceMockitoTests {
    @Mock
    ActivityRepository repository;

    @InjectMocks
    ActivityServiceImpl service;

    @Mock
    private Page<Activity> activityPage;

    @Mock
    private Pageable pageableMock;

    @Test
    @Order(1)
    public void test_getActivityById(){
        Activity activity =new Activity(1L,"name");
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(activity));
        assertEquals(1L,service.findById(1L).getId());
    }

    @Test
    @Order(2)
    public void test_addActivity(){
        Activity activity =new Activity(2L,"name");
        when(repository.save(activity)).thenReturn(activity);
        assertEquals(activity,service.save(activity));
    }

    @Test
    @Order(3)
    public void test_updateActivity(){
        Activity activity =new Activity(2L,"name");
        when(repository.save(activity)).thenReturn(activity);
        assertEquals(activity,service.update(activity));
    }

    @Test
    @Order(4)
    public void test_deleteActivity(){
        Activity activity =new Activity(2L,"name");
        service.delete(activity);
        verify(repository,times(1)).delete(activity);
    }

    @Test
    @Order(5)
    public void test_getAllActivity(){
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(1L,"Activity name 1"));
        activityList.add(new Activity(2L,"Activity name 2"));
        activityPage = new PageImpl(activityList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));

        when(repository.findAll(pageableMock)).thenReturn(activityPage);
        assertEquals(service.getAll(pageableMock).getSize(),2);
    }
}
