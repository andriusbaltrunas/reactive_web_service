package com.reactive.service.service;

import com.reactive.service.exception.ContractCreateException;
import com.reactive.service.exception.ContractNotExistException;
import com.reactive.service.model.Client;
import com.reactive.service.model.ContractStep;
import com.reactive.service.model.dto.*;
import com.reactive.service.repository.ContractRepository;
import com.reactive.service.model.api.ContractApplicant;
import com.reactive.service.model.api.ContractBuyerSeller;
import com.reactive.service.model.api.ContractBuyerSellerInformation;
import lt.sme.finance.sp.poc.model.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final IdGenerateService idGenerateService;

    public ContractService(ContractRepository contractRepository, IdGenerateService idGenerateService) {
        this.contractRepository = contractRepository;
        this.idGenerateService = idGenerateService;
    }

    public Function<String, Flux<Contract>> getContracts() {
        return id -> contractRepository.findById(id)
                .filter(isContractExist())
                .flatMapIterable(Client::getContracts)
                .switchIfEmpty(Flux.empty());
    }

    public Mono<Contract> getContractById(String clientId, String contractId) {
        return contractRepository.findById(clientId)
                .flatMap(createContractsIfNotExist())
                .flatMap(client -> getContractFromClientById(client, contractId))
                .switchIfEmpty(Mono.error(new ContractNotExistException()));
    }

    public Mono<Contract> saveContractApplicant(String clientId, ContractApplicant data) {
        return contractRepository.findById(clientId)
                .flatMap(createContractsIfNotExist())
                .flatMap(client -> createContractApplicant(client, data))
                .switchIfEmpty(Mono.error(new ContractCreateException()));
    }

    public Function<String, Mono<Contract>> getLatestContract() {
        return id -> contractRepository.findById(id)
                .flatMap(getLatestClientContract())
                .switchIfEmpty(Mono.error(new ContractNotExistException()));
    }

    public Mono<Contract> saveContractBuyerOrSeller(String clientId, String contractId, ContractBuyerSellerInformation data) {
        return contractRepository.findById(clientId)
                .flatMap(createContractsIfNotExist())
                .flatMap(client -> createContractBuyerSeller(client, contractId, data))
                .switchIfEmpty(Mono.error(new ContractCreateException()));
    }

    private Mono<Contract> createContractApplicant(Client client, ContractApplicant data) {
        client.getContracts().add(Contract.builder()
                .id(idGenerateService.generate())
                .product(data.product())
                .amount(data.amount())
                .contractStep(ContractStep.APPLICANT)
                .company(Company.builder()
                        .companyId(data.companyId())
                        .name(data.companyName())
                        .country(data.companyCountry())
                        .build())
                .contactPerson(ContactPerson.builder()
                        .name(data.personName())
                        .surname(data.personSurname())
                        .email(data.personEmail())
                        .phone(data.personPhone())
                        .jobPosition(data.jobPosition())
                        .build())
                .timestamp(System.currentTimeMillis())
                .build());

        return contractRepository.save(client)
                .flatMap(getLatestClientContract());
    }

    private Mono<Contract> createContractBuyerSeller(Client client, String contractId, ContractBuyerSellerInformation data) {
        Contract contract = client.getContracts().stream()
                .filter(c -> c.getId().equals(contractId))
                .findFirst()
                .orElseThrow(ContractNotExistException::new);

        client.getContracts().remove(contract);

        client.getContracts().add(contract.toBuilder()
                .buyerInformation(BuyerInformation.builder()
                        .isBuyer(data.isBuyer())
                        .hasInformBuyer(data.hasInformBuyer())
                        .hasInsurance(data.hasInsurance())
                        .buyers(convertToBuyers(data.contractBuyersSellers()))
                        .build())
                .timestamp(System.currentTimeMillis())
                .contractStep(ContractStep.BUYER_SELLER)
                .build());

        return contractRepository.save(client)
                .flatMap(getLatestClientContract());
    }

    private Mono<Contract> getContractFromClientById(Client client, String contractId) {
        return client.getContracts().stream()
                .filter(c -> c.getId().equals(contractId))
                .findFirst()
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    private Function<Client, Mono<Client>> createContractsIfNotExist() {
        return client -> client.getContracts() != null
                ? Mono.just(client)
                : Mono.just(client.toBuilder().contracts(new ArrayList<>()).build());
    }

    private Function<Client, Mono<Contract>> getLatestClientContract() {
        return client -> client.getContracts() != null
                ? Mono.just(client.getContracts().stream().sorted().iterator().next())
                : Mono.empty();
    }

    private Predicate<Client> isContractExist() {
        return client -> client.getContracts() != null;
    }

    private List<Buyer> convertToBuyers(List<ContractBuyerSeller> contractBuyersSellers) {
        return contractBuyersSellers.stream().map(b -> Buyer.builder()
                        .companyName(b.companyName())
                        .country(b.country())
                        .companyCode(b.companyCode())
                        .pvmCode(b.pvmCode())
                        .paymentInterval(b.paymentInterval())
                        .monthlyAmount(b.monthlyAmount())
                        .build())
                .toList();
    }
}
