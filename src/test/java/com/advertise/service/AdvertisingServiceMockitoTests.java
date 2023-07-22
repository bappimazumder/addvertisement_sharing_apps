package com.advertise.service;
import com.advertise.entity.advertisement.Advertising;
import com.advertise.repository.AdvertisingRepository;
import com.advertise.serviceImpl.advertisement.AdvertisingServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AdvertisingServiceMockitoTests.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdvertisingServiceMockitoTests {
    @Mock
    AdvertisingRepository repository;

    @InjectMocks
    AdvertisingServiceImpl service;

    @Mock
    private Page<Advertising> advertisingPage;

    @Mock
    private Pageable pageableMock;


    @Test
    @Order(1)
    public void test_getAdvertisingById(){
        LocalDateTime localDate = LocalDateTime.now();
        BigDecimal budget = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.03");
        Advertising advertising = new Advertising(1L,"R-0001",localDate,localDate,budget,rate,false);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(advertising));
        assertEquals(1L,service.findById(1L).getId());
    }

    @Test
    @Order(2)
    public void test_addAdvertising(){
        LocalDateTime localDate = LocalDateTime.now();
        BigDecimal budget = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.03");
        String targeting = "13-14";
        Advertising advertising = new Advertising("R-0001",localDate,localDate,budget,rate,false,targeting);

        when(repository.save(advertising)).thenReturn(advertising);
        assertEquals(advertising,service.save(advertising));
    }

    @Test
    @Order(3)
    public void test_updateAdvertising(){
        LocalDateTime localDate = LocalDateTime.now();
        BigDecimal budget = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.03");
        Advertising advertising = new Advertising(1L,"R-0001",localDate,localDate,budget,rate,false);
        when(repository.save(advertising)).thenReturn(advertising);
        assertEquals(advertising,service.update(advertising));
    }

    @Test
    @Order(4)
    public void test_deleteAdvertising(){
        LocalDateTime localDate = LocalDateTime.now();
        BigDecimal budget = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.03");
        Advertising advertising = new Advertising(3L,"R-0003",localDate,localDate,budget,rate,false);
        when(repository.save(advertising)).thenReturn(advertising);
    }

    @Test
    @Order(5)
    public void test_getAllAdvertising(){
        List<Advertising> advertisingList = new ArrayList<>();
        LocalDateTime localDate = LocalDateTime.now();
        BigDecimal budget = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.03");
        advertisingList.add(new Advertising(1L,"R-0001",localDate,localDate,budget,rate,false));
        advertisingList.add(new Advertising(2L,"R-0002",localDate,localDate,budget,rate,false));
        advertisingPage = new PageImpl(advertisingList);
        pageableMock = PageRequest.of(0, 2, Sort.by("id"));

        when(repository.findAll(pageableMock)).thenReturn(advertisingPage);
        assertEquals(service.getAll(pageableMock).getSize(),2);
    }
}
