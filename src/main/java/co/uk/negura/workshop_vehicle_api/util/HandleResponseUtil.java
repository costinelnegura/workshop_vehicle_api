package co.uk.negura.workshop_vehicle_api.util;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HandleResponseUtil {

    public enum OperationType {
        CREATE, UPDATE, SEARCH, DELETE
    }

    public ResponseEntity<?> handleResponse(Optional<VehicleEntity> vehicle,
                                            ResponseEntity<?> tokenValidationResponse,
                                            String message,
                                            OperationType operationType) {
        if (tokenValidationResponse.getStatusCode().value() == 200) {
            switch (operationType) {
                case CREATE:
                    return vehicle.isPresent() ?
                            ResponseEntity.status(201).header(
                                    "Message", message
                            ).body(vehicle.get()) :
                            ResponseEntity.status(400).header(
                                    "Message", "Vehicle creation failed"
                            ).build();
                case UPDATE:
                case SEARCH:
                case DELETE:
                    return vehicle.isPresent() ?
                            ResponseEntity.status(200).header(
                                    "Message", message
                            ).body(vehicle.get()) :
                            ResponseEntity.status(404).header(
                                    "Message", "Vehicle not found"
                            ).build();
                default:
                    return ResponseEntity.status(400).header(
                            "Message", "Invalid operation type"
                    ).build();
            }
        } else {
            // Return the failed token validation response
            return tokenValidationResponse.getStatusCode().is4xxClientError() ?
                    ResponseEntity.status(401).header(
                            "Message", "Unauthorized"
                    ).build() :
                    ResponseEntity.status(500).header(
                            "Message", "Internal Server Error"
                    ).build();
        }
    }
}
