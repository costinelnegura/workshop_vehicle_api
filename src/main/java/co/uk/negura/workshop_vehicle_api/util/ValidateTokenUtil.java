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

    /**
     * Validates the provided bearer token.
     * If the token is null or empty, it returns a BAD_REQUEST response.
     * If no instances are found for the service "workshop-users-api", it returns a SERVICE_UNAVAILABLE response.
     * If an error occurs while validating the token, it returns an INTERNAL_SERVER_ERROR response.
     * Otherwise, it returns the response from the token validation API.
     *
     * @param bearerToken the bearer token to validate
     * @return a ResponseEntity with the validation result
     */
    public ResponseEntity<?> validateToken(String bearerToken) {
        Map<String, Object> responseMap = checkBearerToken(bearerToken);
        if (responseMap != null) {
            return new ResponseEntity<>(responseMap, HttpStatus.valueOf((Integer) responseMap.get("status")));
        }

        ServiceInstance instance = getServiceInstance();
        if (instance == null) {
            responseMap = new LinkedHashMap<>();
            responseMap.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            responseMap.put("message", "No instances found for service workshop-users-api");
            return new ResponseEntity<>(responseMap, HttpStatus.SERVICE_UNAVAILABLE);
        }

        URI uri = instance.getUri().resolve(validateTokenApiUrl);
        ResponseEntity<String> responseEntity = sendGetRequest(uri, bearerToken);
        if (responseEntity == null) {
            responseMap = new LinkedHashMap<>();
            responseMap.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMap.put("message", "Error occurred while validating token");
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMap = new LinkedHashMap<>();
        responseMap.put("status", responseEntity.getStatusCode());
        responseMap.put("message", responseEntity.getBody());
        return new ResponseEntity<>(responseMap, responseEntity.getStatusCode());
    }

    /**
     * Checks if the provided bearer token is null or empty.
     * If it is, it returns a map with a BAD_REQUEST status and a message.
     * Otherwise, it returns null.
     *
     * @param bearerToken the bearer token to check
     * @return a map with the check result, or null if the token is valid
     */
    private Map<String, Object> checkBearerToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isEmpty()) {
            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("status", HttpStatus.BAD_REQUEST.value());
            responseMap.put("message", "Bearer token is missing");
            return responseMap;
        }
        return null;
    }

    /**
     * Gets the first service instance for the service "workshop-users-api".
     * If no instances are found, it returns null.
     *
     * @return the first service instance, or null if no instances are found
     */
    private ServiceInstance getServiceInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances("workshop-users-api");
        if (instances.isEmpty()) {
            return null;
        }
        return instances.getFirst();
    }

    /**
     * Sends a GET request to the provided URI with the provided bearer token.
     * The token is included in the Authorization header of the request.
     *
     * @param uri the URI to send the request to
     * @param bearerToken the bearer token to include in the request
     * @return a ResponseEntity with the response from the server
     */
    private ResponseEntity<String> sendGetRequest(URI uri, String bearerToken) {
        Mono<ResponseEntity<String>> responseEntityMono = webClientBuilder.build()
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .retrieve()
                .toEntity(String.class);
        return responseEntityMono.block();
    }
}
