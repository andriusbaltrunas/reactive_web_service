package com.reactive.service.service;

import com.reactive.service.model.Client;
import com.reactive.service.model.api.ClientRequestData;
import com.reactive.service.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    private static final String PHONE = "phone";
    private static final String ID = "id";

    @InjectMocks
    private ClientService service;
    @Mock
    private IdGenerateService idGenerateService;
    @Mock
    private ContractRepository contractRepository;

    @Test
    void testGetClientsWhenClientNotExist() {
        when(contractRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.getClients())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetClientsWhenClientExist() {
        when(contractRepository.findAll()).thenReturn(Flux.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()));

        StepVerifier.create(service.getClients())
                .assertNext(assertClient())
                .verifyComplete();
    }

    @Test
    void testFindClientByIdWhenClientNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.findById().apply(ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindClientByIdWhenClientExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()));

        StepVerifier.create(service.findById().apply(ID))
                .assertNext(assertClient())
                .verifyComplete();
    }

    @Test
    void testGetClientByPhoneWhenClientNotExist() {
        when(contractRepository.findClientByPhone(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.getClientByPhone().apply(new ClientRequestData(PHONE)))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetClientByPhoneWhenClientExist() {
        when(contractRepository.findClientByPhone(anyString())).thenReturn(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()));

        StepVerifier.create(service.getClientByPhone().apply(new ClientRequestData(PHONE)))
                .assertNext(assertClient())
                .verifyComplete();
    }

    @Test
    void testCreateClientWhenClientNotExist() {
        when(idGenerateService.generate()).thenReturn(ID);
        when(contractRepository.findClientByPhone(anyString())).thenReturn(Mono.empty());
        when(contractRepository.save(any(Client.class))).thenReturn(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()));

        StepVerifier.create(service.create().apply(new ClientRequestData(PHONE)))
                .assertNext(assertClient())
                .verifyComplete();
    }

    @Test
    void testCreateClientWhenClientExist() {
        when(contractRepository.save(any(Client.class))).thenReturn(Mono.empty());
        when(contractRepository.findClientByPhone(anyString())).thenReturn(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()));

        StepVerifier.create(service.create().apply(new ClientRequestData(PHONE)))
                .assertNext(assertClient())
                .verifyComplete();
    }

    private Consumer<Client> assertClient() {
        return body -> assertAll(
                () -> assertNotNull(body),
                () -> assertEquals(ID, body.getId()),
                () -> assertEquals(PHONE, body.getPhone())
        );
    }
}