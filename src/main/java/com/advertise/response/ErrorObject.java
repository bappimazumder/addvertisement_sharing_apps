package com.advertise.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorObject implements Serializable {
    private int status;
    private String message;

}
