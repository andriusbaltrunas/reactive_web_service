package com.reactive.service.model.dto;

import com.reactive.service.model.ContractStep;
import com.reactive.service.model.State;
import lombok.*;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Contract implements Comparable<Contract> {
    private String id;
    private String product;
    private Double amount;
    private Company company;
    private ContactPerson contactPerson;
    private BuyerInformation buyerInformation;
    private State state;
    private ContractStep contractStep;
    private long timestamp;

    @Override
    public int compareTo(Contract o) {
        if (timestamp == o.getTimestamp()) {
            return 0;
        }
        return timestamp > o.getTimestamp() ? -1 : 1;
    }
}
