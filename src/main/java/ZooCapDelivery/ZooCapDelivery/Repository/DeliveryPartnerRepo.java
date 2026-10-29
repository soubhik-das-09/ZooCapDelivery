package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPartnerRepo extends MongoRepository<DeliveryPartner, String> {
    DeliveryPartner findDeliveryPartnerById(String id);
    DeliveryPartner findFirstByUsername(String username);
    DeliveryPartner findFirstByName(String name);

    default DeliveryPartner findDeliveryPartnerByUsername(String username) {
        return findFirstByUsername(username);
    }
    default DeliveryPartner findDeliveryPartnerByName(String name) {
        return findFirstByName(name);
    }

    DeliveryPartner findFirstByAvailableTrueAndOnlineTrue();
}
