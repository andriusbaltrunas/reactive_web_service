package com.reactive.service.model.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BuyerInformation {
    private boolean isBuyer;
    private boolean hasInformBuyer;
    private boolean hasInsurance;
    private List<Buyer> buyers;
}
