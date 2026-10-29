package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends MongoRepository<Order, String> {
    List<Order> findByUserName(String userName);
    List<Order> findByRestaurantId(String restaurantId);
    Order findByOrderId(String orderId);
    List<Order> findByDeliverypartnerDetails(String username);

    @org.springframework.data.mongodb.repository.Query("{'deliverypartnerDetails': { $in: [null, []] }, 'status': { $in: ['PREPARING', 'ACCEPTED'] }}")
    List<Order> findUnassignedOrders();
}
