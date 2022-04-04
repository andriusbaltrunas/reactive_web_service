package com.reactive.service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdGenerateServiceTest {
    private Pattern pattern;
    private IdGenerateService service;

    @BeforeAll
    void beforeAll() {
        pattern = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
        service = new IdGenerateService();
    }

    @Test
    void testGenerateId() {
        String id = service.generate();
        assertNotNull(id);
        assertTrue(pattern.matcher(id).matches());
    }
}