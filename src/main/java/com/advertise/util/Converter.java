package com.advertise.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Converter {
    public static <T> MultiValueMap<String, String> toMultiValueMapExcludingNull(T object) {
        HashMap<String, String> hashMap = toHashMapExcludingNull(object);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if(hashMap == null) {
            return multiValueMap;
        }

        for(Map.Entry<String, String> item: hashMap.entrySet()) {
            multiValueMap.put(item.getKey(), Arrays.asList(String.valueOf(item.getValue())));
        }

        return multiValueMap;
    }

    public static <T> HashMap toHashMapExcludingNull(T object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        HashMap<String, Object> map = objectMapper.convertValue(object, HashMap.class);
        return map;
    }
}
