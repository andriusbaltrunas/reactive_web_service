package com.reactive.service.service;

import com.reactive.service.model.Client;
import com.reactive.service.repository.ContractRepository;
import com.reactive.service.model.api.ClientRequestData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class ClientService {
    private final ContractRepository contractRepository;
    private final IdGenerateService idGenerateService;

    public ClientService(ContractRepository contractRepository, IdGenerateService idGenerateService) {
        this.contractRepository = contractRepository;
        this.idGenerateService = idGenerateService;
    }

    public Function<ClientRequestData, Mono<Client>> create() {
        return requestBody -> Mono.justOrEmpty(requestBody)
                .flatMap(getClientByPhone())
                .switchIfEmpty(contractRepository.save(Client.builder()
                        .id(idGenerateService.generate())
                        .phone(requestBody.phone())
                        .build()));
    }

    public Flux<Client> getClients() {
        return contractRepository.findAll();
    }

    public Function<String, Mono<Client>> findById() {
        return contractRepository::findById;
    }

    public Function<ClientRequestData, Mono<Client>> getClientByPhone() {
        return body -> contractRepository.findClientByPhone(body.phone());
    }
}
