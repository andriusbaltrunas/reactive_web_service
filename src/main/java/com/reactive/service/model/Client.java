package com.reactive.service.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.reactive.service.model.dto.Contract;
import org.springframework.cloud.gcp.data.firestore.Document;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@Document(collectionName = "Clients")
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @DocumentId
    private String id;
    private String phone;
    private List<Contract> contracts;
}
