package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;

@Data
public class CartItem {
    private String foodId;

    private String foodName;

    private int quantity;

    private double price;

    private double subTotal;
}
