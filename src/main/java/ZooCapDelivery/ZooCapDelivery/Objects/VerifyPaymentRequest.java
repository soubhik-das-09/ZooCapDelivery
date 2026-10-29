package ZooCapDelivery.ZooCapDelivery.Objects;

import ZooCapDelivery.ZooCapDelivery.Objects.Address;
import lombok.Data;

@Data
public class VerifyPaymentRequest {

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Address address;
}