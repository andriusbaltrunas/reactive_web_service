package com.reactive.service.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContractApplicant(
        @NotBlank
        @JsonProperty("product") String product,
        @JsonProperty("amount") Double amount,
        @JsonProperty("companyName") String companyName,
        @JsonProperty("id") String companyId,
        @JsonProperty("companyCountry") String companyCountry,
        @JsonProperty("personName") String personName,
        @JsonProperty("personSurname") String personSurname,
        @JsonProperty("jobPosition") String jobPosition,
        @JsonProperty("personEmail") String personEmail,
        @JsonProperty("personPhone") String personPhone) {
}
