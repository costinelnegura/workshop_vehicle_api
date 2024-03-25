package co.uk.negura.workshop_vehicle_api.repository;

import co.uk.negura.workshop_vehicle_api.model.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findById(Long id);

    Optional<VehicleEntity> findByRegistration(String registration);
}
