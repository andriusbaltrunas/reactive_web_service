package com.reactive.service.repository;

import com.reactive.service.model.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ContractRepository extends ReactiveCrudRepository<Client, String> {
    Mono<Client> findByContractsId(String id);
    Mono<Client> findClientByPhone(String phone);

}
