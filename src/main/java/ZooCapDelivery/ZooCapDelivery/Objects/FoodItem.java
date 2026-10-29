package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "FoodItem")
@Data
public class FoodItem {
    @Id
    private String id;
    private String restaurantId;
    private String name;

    private Double price;

    private String category;

    private Boolean available;

    private String description;

    private List<String> image;

    private int quantity;

    private Boolean veg;
}
