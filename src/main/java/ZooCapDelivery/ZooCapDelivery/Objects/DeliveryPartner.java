package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "DeliveryPartners")
public class DeliveryPartner {

    @Id
    private String id;

     String deliveryPartnerId;

     String username;

     String password;

     String name;

     String email;

     String phone;
     boolean online;

     boolean available;

     double rating;

     int totalDeliveries =0;

     double todaysEarning =0;

     double totalEarnings;

     String vehicleNumber;

     String vehicleType;

     String licenseNumber;

     Double latitude;

     Double longitude;

     Order currentOrder;

}