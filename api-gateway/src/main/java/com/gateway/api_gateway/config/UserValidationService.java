package com.gateway.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId)
    {
        return userServiceWebClient.get()
                .uri("/api/users/{userId}/valid",userId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<UserResponse> registerUser(RegisterRequest request)
    {
        return  userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}
