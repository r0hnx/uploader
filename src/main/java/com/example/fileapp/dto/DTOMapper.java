package com.example.fileapp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {
    private final ObjectMapper objectMapper;

    public DTOMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T convert(Object source, Class<T> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }
}

