package ZooCapDelivery.ZooCapDelivery.Objects;

import ZooCapDelivery.ZooCapDelivery.Enums.OrderStatus;
import ZooCapDelivery.ZooCapDelivery.Enums.PaymentStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Orders")
@Data
public class Order {
    @Id
    private String orderId;

    private String userName;

    private String restaurantId;

    private List<CartItem> items;

    private double totalAmount;

    private OrderStatus status;

    private LocalDateTime orderTime;

    private Address deliveryAddress;

    private double deliveryFee;

    private double distanceKm;

    private int estimatedTimeMinutes;

    private double finalAmount;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private PaymentStatus paymentStatus;

    private List<String> deliverypartnerDetails;
}