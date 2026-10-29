package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Cart")
@Data
public class Cart {
    @Id
    public String id;

    public String userName;

    public List<CartItem> items = new ArrayList<CartItem>();

    public double totalAmount;
    public  String resturantId;

    public void addToTotal(double amount){
        this.totalAmount += amount;
    }
    public void addItems(CartItem cartItem) {
        this.items.add(cartItem);
    }
    public void subTotals(double price) {
        this.totalAmount -= price;
    }


}

