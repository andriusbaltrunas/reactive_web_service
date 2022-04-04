package com.reactive.service.service;

import com.reactive.service.model.Client;
import com.reactive.service.exception.ContractCreateException;
import com.reactive.service.exception.ContractNotExistException;
import com.reactive.service.model.api.ContractApplicant;
import com.reactive.service.model.api.ContractBuyerSeller;
import com.reactive.service.model.api.ContractBuyerSellerInformation;
import com.reactive.service.model.dto.BuyerInformation;
import com.reactive.service.model.dto.Company;
import com.reactive.service.model.dto.ContactPerson;
import com.reactive.service.model.dto.Contract;
import com.reactive.service.repository.ContractRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    private static final String ID = "id";
    private static final String CLIENT_ID = "client_id";
    private static final String CONTRACT_ID = "contract_id";
    private static final String COMPANY_ID = "company_id";
    private static final String NAME = "name";

    @InjectMocks
    private ContractService contractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private IdGenerateService idGenerateService;

    @Test
    void testGetContractsWhenClientNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(contractService.getContracts().apply(ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetContractsWhenEmptyCollection() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(Collections.emptyList())
                .build()));

        StepVerifier.create(contractService.getContracts().apply(ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetContractsWhenCollectionIsNull() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder().build()));

        StepVerifier.create(contractService.getContracts().apply(ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetContracts() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(Contract.builder().build()))
                .build()));

        StepVerifier.create(contractService.getContracts().apply(ID))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void testGetContractByIdWhenContractNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(contractService.getContractById(CLIENT_ID, CONTRACT_ID))
                .expectError(ContractNotExistException.class)
                .verify();
    }

    @Test
    void testGetContractById() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(createContract()))
                .build()));

        StepVerifier.create(contractService.getContractById(CLIENT_ID, CONTRACT_ID))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testSaveContractApplicantWhenClientNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(contractService.saveContractApplicant(CLIENT_ID, null))
                .expectError(ContractCreateException.class)
                .verify();
    }

    @Test
    void testSaveContractApplicantWhenContractsIsNull() {
        when(idGenerateService.generate()).thenReturn(CONTRACT_ID);
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder().build()));
        when(contractRepository.save(any(Client.class))).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(createContract()))
                .build()));

        StepVerifier.create(contractService.saveContractApplicant(CLIENT_ID, createContractApplicant()))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testSaveContractApplicantWhenContractsIsEmpty() {
        when(idGenerateService.generate()).thenReturn(CONTRACT_ID);
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(new ArrayList<>())
                .build()));
        when(contractRepository.save(any(Client.class))).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(createContract()))
                .build()));

        StepVerifier.create(contractService.saveContractApplicant(CLIENT_ID, createContractApplicant()))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testSaveContractApplicantWhenFewContractsExist() {
        when(idGenerateService.generate()).thenReturn(CONTRACT_ID);
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(new ArrayList<>())
                .build()));

        Contract anotherContract = createContract().toBuilder()
                .id("new_id")
                .timestamp(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond())
                .build();

        when(contractRepository.save(any(Client.class))).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(anotherContract, createContract()))
                .build()));

        StepVerifier.create(contractService.saveContractApplicant(CLIENT_ID, createContractApplicant()))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testGetLatestContract() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(createContract()))
                .build()));

        StepVerifier.create(contractService.getLatestContract().apply(CLIENT_ID))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testGetLatestContractWhenTwoContracts() {
        Contract anotherContract = createContract().toBuilder()
                .id("new_id")
                .timestamp(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond())
                .build();

        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(anotherContract, createContract()))
                .build()));

        StepVerifier.create(contractService.getLatestContract().apply(CLIENT_ID))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertEquals(CONTRACT_ID, resp.getId())
                ))
                .verifyComplete();
    }

    @Test
    void testGetLatestContractWhenClientNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(contractService.getLatestContract().apply(CLIENT_ID))
                .expectError(ContractNotExistException.class)
                .verify();
    }

    @Test
    void testSaveContractBuyerOrSellerWhenClientNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(contractService.saveContractBuyerOrSeller(CLIENT_ID, CONTRACT_ID, createContractBuyerSellerInformation()))
                .expectError(ContractCreateException.class)
                .verify();
    }

    @Test
    void testSaveContractBuyerOrSellerWhenContractNotExist() {
        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder().build()));

        StepVerifier.create(contractService.saveContractBuyerOrSeller(CLIENT_ID, CONTRACT_ID, createContractBuyerSellerInformation()))
                .expectError(ContractNotExistException.class)
                .verify();
    }

    @Test
    void testSaveContractBuyerOrSeller() {
        List<Contract> contracts = new ArrayList<>();
        contracts.add(createContract());

        when(contractRepository.findById(anyString())).thenReturn(Mono.just(Client.builder()
                .contracts(contracts)
                .build()));

        when(contractRepository.save(any(Client.class))).thenReturn(Mono.just(Client.builder()
                .contracts(List.of(createContract()
                        .toBuilder()
                        .buyerInformation(BuyerInformation.builder()
                                .buyers(Collections.emptyList())
                                .build())
                        .build()))
                .build()));

        StepVerifier.create(contractService.saveContractBuyerOrSeller(CLIENT_ID, CONTRACT_ID, createContractBuyerSellerInformation()))
                .assertNext(resp -> assertAll(
                        () -> assertNotNull(resp),
                        () -> assertNotNull(resp.getBuyerInformation()),
                        () -> assertEquals(CONTRACT_ID, resp.getId()),
                        () -> assertTrue(resp.getBuyerInformation().getBuyers().isEmpty())
                ))
                .verifyComplete();
    }

    private Contract createContract() {
        return Contract.builder()
                .id(CONTRACT_ID)
                .company(Company.builder()
                        .companyId(COMPANY_ID)
                        .build())
                .contactPerson(ContactPerson.builder()
                        .name(NAME)
                        .build())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private ContractApplicant createContractApplicant() {
        return new ContractApplicant(
                "product",
                555.5,
                "companyName",
                "id",
                "companyCountry",
                "personName",
                "personSurname",
                "jobPosition",
                "personEmail",
                "personPhone");
    }

    private ContractBuyerSellerInformation createContractBuyerSellerInformation() {
        return new ContractBuyerSellerInformation(
                true,
                true,
                true,
                List.of(new ContractBuyerSeller(
                        "companyName",
                        "country",
                        "companyCode",
                        "pvmCode",
                        10,
                        55.5)));
    }
}