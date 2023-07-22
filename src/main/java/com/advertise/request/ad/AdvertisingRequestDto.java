package com.advertise.request.ad;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdvertisingRequestDto {
  //  private String reference;
    @ApiParam(name = "name", value = "Name is Mandatory", required = true)
    private LocalDateTime endDate;
    private BigDecimal budget;
    private String descriptionAdd;
    @NotNull(message = "status cannot be null")
    private String status;
    @NotNull(message = "targeting cannot be null")
    private TargetingRequestDto targeting;
    @NotNull(message = "link cannot be null")
    private String linkUrlEnterpriseProduct;
    @NotNull(message = "title cannot be null")
    private String title;
    @NotNull(message = "Company Description cannot be null")
    private String companyDescription;
    private String sampleTitle;
    private String sampleDescription;

}
