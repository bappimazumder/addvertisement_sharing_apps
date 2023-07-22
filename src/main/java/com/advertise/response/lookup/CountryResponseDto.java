package com.advertise.response.lookup;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class CountryResponseDto {
    private Long id;
    private String name;
    private String countryCode;
    private Date createTime;
    private Date editTime;
    private String editedBy;
    private String createdBy;
}
