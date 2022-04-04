package com.reactive.service.router;

import com.reactive.service.model.Client;
import com.reactive.service.router.handler.ClientRouterHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ClientRouterTest {
    private static final String ID = "id";
    private static final String PHONE = "phone";
    @Mock
    private ClientRouterHandler clientRouterHandler;
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        RouterFunction<ServerResponse> route = new ClientRouter().createClient(clientRouterHandler);
        webClient = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    void testGetClientsWhenListEmpty() {
        when(clientRouterHandler.getClients(any(ServerRequest.class))).thenReturn(Mono.empty());

        webClient.get()
                .uri(ClientRouter.ROOT_ROUTE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }

    @Test
    void testGetClients() {
        when(clientRouterHandler.getClients(any(ServerRequest.class))).thenReturn(ServerResponse.ok().body(Flux.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()), Client.class));

        String json = """
                [
                    {
                        "id": "id",
                        "phone": "phone",
                        "contracts": null
                    }
                ]
                """;

        webClient.get()
                .uri(ClientRouter.ROOT_ROUTE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(json);
    }

    @Test
    void testCreateClient() {
        when(clientRouterHandler.createClient(any(ServerRequest.class))).thenReturn(ServerResponse.ok().body(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()), Client.class));

        String json = """
                {
                    "id": "id",
                    "phone": "phone",
                    "contracts": null
                }
                """;

        webClient.post()
                .uri(ClientRouter.ROOT_ROUTE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(json);
    }

    @Test
    void testGetClientByIdWhenClientNotExist() {
        when(clientRouterHandler.getClientById(any(ServerRequest.class))).thenReturn(ServerResponse.notFound().build());

        webClient.get()
                .uri(ClientRouter.ROOT_ROUTE + "/" + ID)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testGetClientById() {
        when(clientRouterHandler.getClientById(any(ServerRequest.class))).thenReturn(ServerResponse.ok().body(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()), Client.class));

        String json = """
                {
                    "id": "id",
                    "phone": "phone",
                    "contracts": null
                }
                """;

        webClient.get()
                .uri(ClientRouter.ROOT_ROUTE + "/" + ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(json);
    }

    @Test
    void testGetClientByPhoneWhenClientNotExist() {
        when(clientRouterHandler.getClientByPhone(any(ServerRequest.class))).thenReturn(ServerResponse.notFound().build());

        webClient.post()
                .uri(ClientRouter.ROOT_ROUTE + "/phone")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("""
                            {
                                "phone": "phone"
                            }
                        """))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testGetClientByPhone() {

        when(clientRouterHandler.getClientByPhone(any(ServerRequest.class))).thenReturn(ServerResponse.ok().body(Mono.just(Client.builder()
                .id(ID)
                .phone(PHONE)
                .build()), Client.class));

        String json = """
                {
                    "id": "id",
                    "phone": "phone",
                    "contracts": null
                }
                """;

        webClient.post()
                .uri(ClientRouter.ROOT_ROUTE + "/phone")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("""
                            {
                                "phone": "phone"
                            }
                        """))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(json);
    }
}