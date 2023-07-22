package com.advertise.response.ad;

import lombok.Data;

@Data
public class MediaResponseDto {
    private long id;
    private String linkHttp;
    private Integer imageCategory;
    private String fileType;
}
