package co.uk.negura.workshop_vehicle_api.controller;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import co.uk.negura.workshop_vehicle_api.service.VehicleService;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /*
    Create a new vehicle and save the vehicle details.
     */
    @PostMapping()
    public ResponseEntity<?> createVehicle(@RequestBody VehicleEntity vehicleEntity,
                                            @RequestHeader(value="Authorization") String bearerToken){
        return vehicleService.createVehicle(vehicleEntity, bearerToken);
    }

    /*
    Update vehicle details by using the ID to find it and update it with the new vehicle details from the JsonPatch.
     */
    @PatchMapping("/{ID}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long ID,
                                            @RequestBody JsonPatch patch,
                                            @RequestHeader(value="Authorization") String bearerToken){
        return vehicleService.updateVehicle(ID, patch, bearerToken);
    }

    /*
    Search for a vehicle using the ID or the email, can be potentially extended to search for vehicle based on other parameters.
     */
    @GetMapping()
    public ResponseEntity<?> searchVehicle(@RequestBody Map<String, String> searchRequest,
                                            @RequestHeader(value="Authorization") String bearerToken){
        return vehicleService.searchVehicle(searchRequest, bearerToken);
    }

    /*
    Delete vehicle using the ID, can be potentially extended to delete vehicle based on other parameters.
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteVehicle(@RequestBody Map<String, Long> searchRequest,
                                            @RequestHeader(value="Authorization") String bearerToken){
        return vehicleService.deleteVehicle(searchRequest, bearerToken);
    }
}
