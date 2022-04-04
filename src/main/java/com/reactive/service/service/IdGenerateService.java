package com.reactive.service.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGenerateService {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
