package com.advertise.response.ad;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdvertisingResponseDto {
    private long id;
    private String reference;
    private String startDate;
    private String endDate;
    private BigDecimal budget;
    private BigDecimal rate;
    private Boolean isHeaderLocation;
    private String targeting;
    private String status;
    private String linkUrlEnterpriseProduct;
    private String title;
    private String companyName;
    private String companyDescription;
    private String sampleTitle;
    private String sampleDescription;
    private List<MediaResponseDto> mediaList = new ArrayList<>();
}
