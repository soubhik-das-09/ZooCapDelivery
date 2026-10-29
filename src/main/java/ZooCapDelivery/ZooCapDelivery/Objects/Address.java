package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;

@Data
public class Address {

    private String houseNo;        // Flat / Door number

    private String societyName;    // Society / Apartment complex (optional)

    private String buildingName;   // Building / Tower name (optional)

    private String street;         // Street / Area / Road

    private String landmark;       // Landmark (optional)

    private String city;

    private String state;

    private String pincode;

    private String phoneNumber;

    private Double latitude;

    private Double longitude;
}