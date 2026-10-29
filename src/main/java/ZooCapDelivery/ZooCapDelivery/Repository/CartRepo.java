package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends MongoRepository<Cart,String> {
  public Cart findByUserName(String userName);
}
