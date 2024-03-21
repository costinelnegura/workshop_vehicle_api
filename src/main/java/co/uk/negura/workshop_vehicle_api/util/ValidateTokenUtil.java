package co.uk.negura.workshop_vehicle_api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidateTokenUtil {

    private final WebClient.Builder webClientBuilder;

    private final DiscoveryClient discoveryClient;

    @Value("${auth.validateTokenApiUrl}")
    private String validateTokenApiUrl;

    public ValidateTokenUtil(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }


    public ResponseEntity<?> validateToken(String bearerToken) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        if (bearerToken == null || bearerToken.isEmpty()) {
            responseMap.put("status", HttpStatus.BAD_REQUEST.value());
            responseMap.put("message", "Bearer token is missing");
            return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("workshop-users-api");
        if (instances.isEmpty()) {
            responseMap.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            responseMap.put("message", "No instances found for service workshop-users-api");
            return new ResponseEntity<>(responseMap, HttpStatus.SERVICE_UNAVAILABLE);
        }

        ServiceInstance instance = instances.getFirst();
        URI uri = instance.getUri().resolve(validateTokenApiUrl);

        Mono<ResponseEntity<String>> responseEntityMono = webClientBuilder.build()
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .retrieve()
                .toEntity(String.class);

        ResponseEntity<String> responseEntity = responseEntityMono.block();
        if (responseEntity == null) {
            responseMap.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMap.put("message", "Error occurred while validating token");
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMap.put("status", responseEntity.getStatusCode());
        responseMap.put("message", responseEntity.getBody());
        return new ResponseEntity<>(responseMap, responseEntity.getStatusCode());
    }
}
