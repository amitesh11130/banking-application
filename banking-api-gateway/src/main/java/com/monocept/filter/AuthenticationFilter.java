package com.monocept.filter;

import com.monocept.util.JwtUtil;
import com.monocept.exception.AuthorizationHeaderMissingException;
import com.monocept.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            if (validator.isSecured.test(request)) {
                log.info("Request to secured route received. Checking for Authorization Header...");


                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.warn("Authorization Header is missing in the request");
                    throw new AuthorizationHeaderMissingException("Missing Authorization Header");
                }

                String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                    log.debug("Authorization header found, extracted token: {}", authHeader);
                } else {
                    log.warn("Authorization header format is invalid. Expected format: Bearer <token>");
                    throw new UnAuthorizedException("Unauthorized access to application");
                }
                try {
                    log.info("Validating JWT token...");
                    jwtUtil.validToken(authHeader);
                    log.info("JWT token successfully validated.");
                } catch (Exception e) {
                    log.error("Token validation failed: {}", e.getMessage());
                    throw new UnAuthorizedException("Unauthorized access to application");
                }

            }
            return chain.filter(exchange);
        };
    }

}
