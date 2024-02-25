package co.uk.negura.workshop_vehicle_api.service;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import co.uk.negura.workshop_vehicle_api.repository.VehicleRepository;
import co.uk.negura.workshop_vehicle_api.util.HandleResponseUtil;
import co.uk.negura.workshop_vehicle_api.util.ValidateTokenUtil;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ValidateTokenUtil validateTokenUtil;

    @Autowired
    private HandleResponseUtil handleResponseUtil;

    public ResponseEntity<?> createVehicle(VehicleEntity vehicleEntity, String bearerToken) {
        return null;
    }

    public ResponseEntity<?> updateVehicle(Long id, JsonPatch patch, String bearerToken) {
        return null;
    }

    public ResponseEntity<?> searchVehicle(Map<String, String> searchRequest, String bearerToken) {
        return null;
    }

    public ResponseEntity<?> deleteVehicle(Map<String, Long> searchRequest, String bearerToken) {
        return null;
    }
}
