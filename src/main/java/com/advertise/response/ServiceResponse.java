package com.advertise.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse <T> implements Serializable{
    private Metadata meta;
    private T data;
    private HttpStatus status;

    private Pagination pagination;
    public ServiceResponse(Metadata meta, T data) {
        this.meta = meta;
        this.data = data;
    }

    public ServiceResponse(Metadata meta, T data,HttpStatus status) {
        this.meta = meta;
        this.data = data;
        this.status=status;
    }
}