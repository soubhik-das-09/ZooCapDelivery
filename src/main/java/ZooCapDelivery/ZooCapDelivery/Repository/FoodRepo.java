package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepo extends MongoRepository<FoodItem, String> {
    List<FoodItem> findByRestaurantId(String restaurantId);
    List<FoodItem> findByCategory(String category);
    FoodItem findByName(String foodName);
    List<FoodItem> findByVeg(Boolean veg);
    List<FoodItem> findByPriceBetween(
            Double min,
            Double max
    );
    List<FoodItem> findByCategoryIgnoreCase(String category);
    //FoodItem findById(String foodName)
}
