package com.reactive.service.model.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Validation {
    @JsonProperty("code")
    private int code;
    @JsonProperty("field")
    private String field;
    @JsonProperty("message")
    private String message;
}
