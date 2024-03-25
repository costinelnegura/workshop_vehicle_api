package co.uk.negura.workshop_vehicle_api.controller;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import co.uk.negura.workshop_vehicle_api.service.VehicleService;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Create a new vehicle and save the vehicle details.
     * @param vehicleEntity the vehicle details to be saved
     * @return the response entity containing the vehicle details
     */
    @PostMapping()
    public ResponseEntity<?> createVehicle(@RequestBody VehicleEntity vehicleEntity){
        return vehicleService.createVehicle(vehicleEntity);
    }

    /**
     * Update a vehicle using the ID and the patch request.
     * @param ID the ID of the vehicle to be updated
     * @param patch the patch request containing the details to be updated
     * @return the response entity containing the updated vehicle details
     */
    @PatchMapping("/{ID}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long ID,
                                            @RequestBody JsonPatch patch){
        return vehicleService.updateVehicle(ID, patch);
    }

    /**
     * Search for a vehicle using the ID or the registration, can be potentially extended to search for vehicle based on other parameters.
     * @param searchRequest the search request containing the ID or registration of the vehicle to be searched from the database.
     * @return the response entity containing the vehicle details
     */
    @GetMapping()
    public ResponseEntity<?> searchVehicle(@RequestBody Map<String, String> searchRequest) {
        return vehicleService.searchVehicle(searchRequest);
    }

    /**
     * Get all vehicles from the database.
     * @return the response entity containing all the vehicle details
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllVehicles(){
        return vehicleService.getAllVehicles();
    }

    /**
     * Delete a vehicle using the ID or the registration.
     * @param searchRequest the search request containing the ID or registration of the vehicle to be deleted from the database.
     * @return the response entity containing the status of the deletion
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteVehicle(@RequestBody Map<String, String> searchRequest){
        return vehicleService.deleteVehicle(searchRequest);
    }
}
