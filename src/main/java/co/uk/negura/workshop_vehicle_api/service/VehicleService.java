package co.uk.negura.workshop_vehicle_api.service;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import co.uk.negura.workshop_vehicle_api.repository.VehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Validate the token and return the response entity.
     * @param vehicleEntity VehicleEntity object to be saved.
     * @return ResponseEntity with the status of the request.
     */
    public ResponseEntity<?> createVehicle(VehicleEntity vehicleEntity) {
        Map<String, Object> map = new LinkedHashMap<>();
        if(vehicleRepository.findByRegistration(vehicleEntity.getRegistration()).isPresent()){
            map.put("object", "error");
            map.put("status", "400");
            map.put("message", "Vehicle already exists");
            return ResponseEntity.badRequest().body(map);
        }else{
            VehicleEntity newVehicle = vehicleRepository.save(vehicleEntity);
            return ResponseEntity.ok().body(newVehicle);
        }
    }

    /**
     * Update vehicle details by using the ID to find it and update it with the new vehicle details from the JsonPatch.
     * @param id ID of the vehicle to be updated.
     * @param patch JsonPatch object to be applied to the vehicle.
     * @return ResponseEntity with the status of the request.
     */
    public ResponseEntity<?> updateVehicle(Long id, JsonPatch patch) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            if(!vehicleRepository.existsById(id)){
                map.put("object", "error");
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", "Vehicle not found");
                return ResponseEntity.badRequest().body(map);
            }
            VehicleEntity vehicle = vehicleRepository.findById(id).orElseThrow();
            VehicleEntity patchedVehicle = applyPatchToVehicle(patch, vehicle);
            vehicleRepository.save(patchedVehicle);
            return ResponseEntity.ok().body(patchedVehicle);
        } catch (JsonPatchException | JsonProcessingException e) {
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "Error updating vehicle, check the request body.");
            return ResponseEntity.badRequest().body(map);
        }
    }

    /**
     * Apply the JsonPatch to the vehicle.
     * @param patch JsonPatch object to be applied to the vehicle.
     * @param targetVehicle VehicleEntity object to be updated.
     * @return VehicleEntity object with the updated details.
     */
    private VehicleEntity applyPatchToVehicle(JsonPatch patch, VehicleEntity targetVehicle)
            throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetVehicle, JsonNode.class));
        return new ObjectMapper().treeToValue(patched, VehicleEntity.class);
    }

    /**
     * Search for a vehicle using the registration or ID.
     * @param searchRequest Map containing the search parameters.
     * @return ResponseEntity with the status of the request.
     */
    public ResponseEntity<?> searchVehicle(Map<String, String> searchRequest) {
        Map<String, Object> map = new LinkedHashMap<>();
            if (searchRequest.containsKey("registration")) {
            if(searchRequest.get("registration").isEmpty()){
                map.put("object", "error");
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", "Bad Request, vehicle registration is required");
                return ResponseEntity.badRequest().body(map);
            }
            String registration = searchRequest.get("registration");
            VehicleEntity vehicle = vehicleRepository.findByRegistration(registration).orElse(null);
                return getResponseEntity(map, vehicle);
            } else if (searchRequest.containsKey("id")) {
            if(searchRequest.get("id").isEmpty()){
                map.put("object", "error");
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", "Bad Request, vehicle ID is required");
                return ResponseEntity.badRequest().body(map);
            }
            Long id = Long.parseLong(searchRequest.get("id"));
            VehicleEntity vehicle = vehicleRepository.findById(id).orElse(null);
                return getResponseEntity(map, vehicle);
            } else {
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "Bad Request");
            return ResponseEntity.badRequest().body(map);
        }
    }

    /**
     * Get the response entity based on the vehicle found or not.
     * @param map Map containing the response details.
     * @param vehicle VehicleEntity object found.
     * @return ResponseEntity with the status of the request.
     */
    private ResponseEntity<?> getResponseEntity(Map<String, Object> map, VehicleEntity vehicle) {
        if(vehicle == null){
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "Vehicle not found");
            return ResponseEntity.badRequest().body(map);
        }
        return ResponseEntity.ok().body(vehicle);
    }

    /**
     * Delete vehicle details using the registration or ID.
     * @param searchRequest the search request containing the registration or ID of the vehicle to be deleted from the database
     * @return ResponseEntity with the status of the request.
     */
    public ResponseEntity<?> deleteVehicle(Map<String, String> searchRequest) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (searchRequest.containsKey("registration")) {
            if(searchRequest.get("registration").isEmpty()){
                map.put("object", "error");
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", "Bad Request, vehicle registration is required");
                return ResponseEntity.badRequest().body(map);
            }
            String registration = searchRequest.get("registration");
            VehicleEntity vehicle = vehicleRepository.findByRegistration(registration).orElse(null);
            return getDeleteResponseEntity(map, vehicle);
        } else if (searchRequest.containsKey("id")) {
            if(searchRequest.get("id").isEmpty()){
                map.put("object", "error");
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("message", "Bad Request, vehicle ID is required");
                return ResponseEntity.badRequest().body(map);
            }
            Long id = Long.parseLong(searchRequest.get("id"));
            VehicleEntity vehicle = vehicleRepository.findById(id).orElse(null);
            return getDeleteResponseEntity(map, vehicle);
        } else {
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "Bad Request");
            return ResponseEntity.badRequest().body(map);
        }
    }

    /**
     * Get the response entity based on the vehicle found or not.
     * @param map Map containing the response details.
     * @param vehicle VehicleEntity object found.
     * @return ResponseEntity with the status of the request.
     */
    private ResponseEntity<?> getDeleteResponseEntity(Map<String, Object> map, VehicleEntity vehicle) {
        if(vehicle == null){
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "Vehicle not found");
            return ResponseEntity.badRequest().body(map);
        }
        vehicleRepository.delete(vehicle);
        map.put("object", "success");
        map.put("status", HttpStatus.OK);
        map.put("message", "Vehicle deleted successfully");
        return ResponseEntity.ok().body(map);
    }

    /**
     * Get all vehicles from the database.
     * @return ResponseEntity with the status of the request.
     */
    public ResponseEntity<?> getAllVehicles() {
        if(vehicleRepository.findAll().isEmpty()){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("object", "error");
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "No vehicles found");
            return ResponseEntity.badRequest().body(map);
        }
        return ResponseEntity.ok().body(vehicleRepository.findAll());
    }
}
