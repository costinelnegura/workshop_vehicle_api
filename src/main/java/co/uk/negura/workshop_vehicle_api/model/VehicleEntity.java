package co.uk.negura.workshop_vehicle_api.model;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String registration;
    @Column(nullable = false)
    private String make;
    @Column(nullable = false)
    private String model;
    private String colour;
    private String colourCode;
    private boolean isDrivable;
    private String VIN;
    private String engineSize;
    private String fuelType;
    private String transmission;
    private String bodyType;
    private String year;
    private String mileage;


}
