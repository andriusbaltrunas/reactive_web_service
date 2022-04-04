package com.reactive.service.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContractBuyerSeller(
        @JsonProperty("companyName") String companyName,
        @JsonProperty("country") String country,
        @JsonProperty("companyCode") String companyCode,
        @JsonProperty("pvmCode") String pvmCode,
        @JsonProperty("paymentInterval") int paymentInterval,
        @JsonProperty("monthlyAmount") Double monthlyAmount) {
}