package com.monocept.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndPoints = List.of(
            "/api/v1/secure/register",
            "/api/v1/secure/generateToken",
            "/api/v1/secure/refreshToken",
            "/api/v1/secure/getAll",
            "/eureka",
            "/actuator/health"
    );

    public Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndPoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

}
