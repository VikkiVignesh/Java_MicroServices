package com.gateway.api_gateway.config;

import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
    private  final UserValidationService userService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
         String userId=exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");

        RegisterRequest registerUser=getUserDetails(token);
        if(userId==null)
        {
            userId=registerUser.getKeyCloakId();
        }

        if(userId !=null && token !=null)
        {
            String finalUserId=userId;
            return userService.validateUser(userId)
                    .flatMap(exist ->{
                        if(!exist)
                        {

                            if(registerUser!=null)
                            {
                                return  userService.registerUser(registerUser).then(Mono.empty());

                            }
                            return Mono.empty();
                        }
                        else {
                            log.info("User already Exists , Skipping sync with keycloak");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest=exchange.getRequest().mutate()
                                .header("X-User-ID",finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
     try{
         String tokenWithoutBearer=token.replace("Bearer","").trim();
         SignedJWT signedJWT=SignedJWT.parse(tokenWithoutBearer);
         JWTClaimsSet claims=signedJWT.getJWTClaimsSet();

         RegisterRequest register=new RegisterRequest();
         register.setEmail(claims.getClaimAsString("email"));
         register.setPassword("Test@123");
         register.setRole("USER");
         register.setFirstName(claims.getClaimAsString("given_name"));
         register.setLastName(claims.getClaimAsString("family_name"));
         register.setKeyCloakId(claims.getClaimAsString("sub"));
         return register;
     }
     catch (Exception e)
     {
         e.printStackTrace();
         return null;
     }
    }
}
