package com.reactive.service.model.api;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContractBuyerSellerInformation(
        @JsonProperty("isBuyer") boolean isBuyer,
        @JsonProperty("hasInformBuyer") boolean hasInformBuyer,
        @JsonProperty("hasInsurance") boolean hasInsurance,
        @JsonProperty("contractBuyersSellers") List<ContractBuyerSeller> contractBuyersSellers) {
}
