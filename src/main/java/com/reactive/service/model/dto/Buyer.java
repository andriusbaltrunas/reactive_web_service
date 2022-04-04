package com.reactive.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Buyer {
    private String companyName;
    private String country;
    private String companyCode;
    private String pvmCode;
    private int paymentInterval;
    private Double monthlyAmount;
}