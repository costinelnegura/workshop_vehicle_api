package co.uk.negura.workshop_vehicle_api.service;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import co.uk.negura.workshop_vehicle_api.repository.VehicleRepository;
import co.uk.negura.workshop_vehicle_api.util.ValidateTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ValidateTokenUtil validateTokenUtil;

    /*
    Validate bearer token.
     */
    private ResponseEntity<?> validateToken(String bearerToken) {
        return validateTokenUtil.validateToken(bearerToken);
    }

    /*
    Create a new vehicle and save the vehicle details.
     */
    public ResponseEntity<?> createVehicle(VehicleEntity vehicleEntity, String bearerToken) {
        ResponseEntity<?> tokenValidationResponse = validateToken(bearerToken);
        Map<String, Object> map = new LinkedHashMap<>();
        if (tokenValidationResponse.getStatusCode().value() != 200) {
            return tokenValidationResponse;
        }
        if(vehicleRepository.findByRegistration(vehicleEntity.getRegistration()).isPresent()){
            map.put("status", "0");
            map.put("message", "Vehicle already exists");
            return ResponseEntity.badRequest().body(map);
        }else{
            VehicleEntity newVehicle = vehicleRepository.save(vehicleEntity);
            map.put("status", "1");
            map.put("message", "Vehicle created successfully");
            map.put("data", newVehicle);
            return ResponseEntity.ok().body(map);
        }
    }

    /*
    Update vehicle details by using the ID to find it and update it with the new vehicle details from the JsonPatch.
     */
    public ResponseEntity<?> updateVehicle(Long id, JsonPatch patch, String bearerToken) {
        ResponseEntity<?> tokenValidationResponse = validateToken(bearerToken);
        Map<String, Object> map = new LinkedHashMap<>();
        if (tokenValidationResponse.getStatusCode().value() != 200) {
            return tokenValidationResponse;
        }
        try {
            if(!vehicleRepository.existsById(id)){
                map.put("status", "0");
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            VehicleEntity vehicle = vehicleRepository.findById(id).orElseThrow();
            VehicleEntity patchedVehicle = applyPatchToVehicle(patch, vehicle);
            vehicleRepository.save(patchedVehicle);
            map.put("status", "1");
            map.put("message", "Vehicle updated successfully");
            map.put("data", patchedVehicle);
            return ResponseEntity.ok().body(map);
        } catch (JsonPatchException | JsonProcessingException e) {
            map.put("status", "0");
            map.put("message", "Error updating vehicle");
            return ResponseEntity.badRequest().body(map);
        }
    }

    /*
    Apply the patch to the vehicle object.
     */
    private VehicleEntity applyPatchToVehicle(JsonPatch patch, VehicleEntity targetVehicle)
            throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetVehicle, JsonNode.class));
        return new ObjectMapper().treeToValue(patched, VehicleEntity.class);
    }

    /*
    Search for a vehicle using the ID or the registration, can be potentially extended to search for vehicle based on other parameters.
     */
    public ResponseEntity<?> searchVehicle(Map<String, String> searchRequest, String bearerToken) {
        ResponseEntity<?> tokenValidationResponse = validateToken(bearerToken);
        Map<String, Object> map = new LinkedHashMap<>();
        if (tokenValidationResponse.getStatusCode().value() != 200) {
            return tokenValidationResponse;
        } else if (searchRequest.containsKey("registration")) {
            String registration = searchRequest.get("registration");
            VehicleEntity vehicle = vehicleRepository.findByRegistration(registration).orElse(null);
            if(vehicle == null){
                map.put("status", "0");
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            map.put("status", "1");
            map.put("message", "Vehicle retrieved successfully");
            map.put("data", vehicle);
            return ResponseEntity.ok().body(map);
        } else if (searchRequest.containsKey("id")) {
            Long id = Long.parseLong(searchRequest.get("id"));
            VehicleEntity vehicle = vehicleRepository.findById(id).orElse(null);
            if(vehicle == null){
                map.put("status", "0");
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            map.put("status", "1");
            map.put("message", "Vehicle retrieved successfully");
            map.put("data", vehicle);
            return ResponseEntity.ok().body(map);
        } else {
            map.put("status", "0");
            map.put("message", "Bad Request");
            return ResponseEntity.badRequest().body(map);
        }

    }

    /*
    Delete vehicle using the ID or registration, can be potentially extended to delete vehicle based on other parameters.
     */
    public ResponseEntity<?> deleteVehicle(Map<String, String> searchRequest, String bearerToken) {
        ResponseEntity<?> tokenValidationResponse = validateToken(bearerToken);
        Map<String, Object> map = new LinkedHashMap<>();
        if (tokenValidationResponse.getStatusCode().value() != 200) {
            return tokenValidationResponse;
        } else if (searchRequest.containsKey("registration")) {
            String registration = searchRequest.get("registration");
            VehicleEntity vehicle = vehicleRepository.findByRegistration(registration).orElse(null);
            if(vehicle == null){
                map.put("status", "0");
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            vehicleRepository.delete(vehicle);
            map.put("status", "1");
            map.put("message", "Vehicle deleted successfully");
            return ResponseEntity.ok().body(map);
        } else if (searchRequest.containsKey("id")) {
            Long id = Long.parseLong(searchRequest.get("id"));
            VehicleEntity vehicle = vehicleRepository.findById(id).orElse(null);
            if(vehicle == null){
                map.put("status", "0");
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            vehicleRepository.delete(vehicle);
            map.put("status", "1");
            map.put("message", "Vehicle deleted successfully");
            return ResponseEntity.ok().body(map);
        } else {
            map.put("status", "0");
            map.put("message", "Bad Request");
            return ResponseEntity.badRequest().body(map);
        }
    }
}
