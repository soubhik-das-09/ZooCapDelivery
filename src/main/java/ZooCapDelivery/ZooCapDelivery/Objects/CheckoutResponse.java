package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {

    private String razorpayOrderId;

    private String key;

    private long amount;

    private String currency;

    private double distanceKm;

    private int estimatedTimeMinutes;

    private double deliveryFee;

    private double finalAmount;
}