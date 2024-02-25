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

    public VehicleEntity(Long id, String registration, String make, String model, String colour, String colourCode, boolean isDrivable, String VIN, String engineSize, String fuelType, String transmission, String bodyType, String year, String mileage) {
        this.id = id;
        this.registration = registration;
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.colourCode = colourCode;
        this.isDrivable = isDrivable;
        this.VIN = VIN;
        this.engineSize = engineSize;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.bodyType = bodyType;
        this.year = year;
        this.mileage = mileage;
    }

    public VehicleEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    public boolean isDrivable() {
        return isDrivable;
    }

    public void setDrivable(boolean drivable) {
        isDrivable = drivable;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }
}
