package com.reactive.service.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;


public record ClientRequestData(@NotEmpty @JsonProperty("phone") String phone) {
}
