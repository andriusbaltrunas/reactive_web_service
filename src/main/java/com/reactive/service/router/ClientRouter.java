package com.reactive.service.router;

import com.reactive.service.router.handler.ClientRouterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ClientRouter {

    protected static final String ROOT_ROUTE = "/clients";

    @Bean
    public RouterFunction<ServerResponse> createClient(ClientRouterHandler clientRouterHandler) {
        return route(POST(ROOT_ROUTE).and(accept(MediaType.APPLICATION_JSON)), clientRouterHandler::createClient)
                .andRoute(GET(ROOT_ROUTE), clientRouterHandler::getClients)
                .andRoute(GET(ROOT_ROUTE + "/{id}"), clientRouterHandler::getClientById)
                .andRoute(POST(ROOT_ROUTE + "/phone").and(accept(MediaType.APPLICATION_JSON)), clientRouterHandler::getClientByPhone);
    }
}