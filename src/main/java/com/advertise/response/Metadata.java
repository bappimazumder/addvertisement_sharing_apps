package com.advertise.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Metadata implements Serializable {
    String code;
    String description;
}