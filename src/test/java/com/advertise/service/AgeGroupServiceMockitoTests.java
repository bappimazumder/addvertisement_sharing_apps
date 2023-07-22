package com.advertise.service;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.repository.AgeGroupRepository;
import com.advertise.serviceImpl.advertisement.AgeGroupServiceImpl;
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
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AgeGroupServiceMockitoTests.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgeGroupServiceMockitoTests {

    @Mock
    AgeGroupRepository repository;

    @InjectMocks
    AgeGroupServiceImpl service;

    @Mock
    private Page<AgeGroup> ageGroupPage;

    @Mock
    private Pageable pageableMock;

    @Test
    @Order(1)
    public void test_getAgeGroupById(){
        AgeGroup ageGroup =new AgeGroup(1L,"value 1");
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(ageGroup));
        assertEquals(1L,service.findById(1L).getId());
    }

    @Test
    @Order(2)
    public void test_addAgeGroup(){
        AgeGroup ageGroup =new AgeGroup(2L,"value 2");
        when(repository.save(ageGroup)).thenReturn(ageGroup);
        assertEquals(ageGroup,service.save(ageGroup));
    }

    @Test
    @Order(3)
    public void test_updateAgeGroup(){
        AgeGroup ageGroup =new AgeGroup(2L,"value 2");
        when(repository.save(ageGroup)).thenReturn(ageGroup);
        assertEquals(ageGroup,service.update(ageGroup));
    }

    @Test
    @Order(4)
    public void test_deleteAgeGroup(){
        AgeGroup ageGroup =new AgeGroup(2L,"name");
        service.delete(ageGroup);
        verify(repository,times(1)).delete(ageGroup);
    }

    @Test
    @Order(5)
    public void test_getAllAgeGroup(){
        List<AgeGroup> AgeGroupList = new ArrayList<>();
        AgeGroupList.add(new AgeGroup(1L,"AgeGroup value 1"));
        AgeGroupList.add(new AgeGroup(2L,"AgeGroup value 2"));
        ageGroupPage = new PageImpl(AgeGroupList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));

        when(repository.findAll(pageableMock)).thenReturn(ageGroupPage);
        assertEquals(service.getAll(pageableMock).getSize(),2);
    }

}
