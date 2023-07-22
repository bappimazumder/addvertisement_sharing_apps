package com.advertise.request.ad;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class TargetingRequestDto {
    private List<String> country = new ArrayList<>();
    private List<String> region = new ArrayList<>();
    private List<String> ageGroup = new ArrayList<>();
    private List<String> activity = new ArrayList<>();
    private List<String> gender = new ArrayList<>();
}
