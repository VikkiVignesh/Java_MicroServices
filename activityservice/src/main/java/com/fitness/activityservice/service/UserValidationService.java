package com.fitness.activityservice.service;

import com.fitness.activityservice.config.WebClientConfig;
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
}
