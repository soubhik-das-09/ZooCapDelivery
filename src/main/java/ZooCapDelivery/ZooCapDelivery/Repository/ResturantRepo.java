package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResturantRepo extends MongoRepository<Resturant, String> {
    Resturant findByResturantName(String resturantName);

    List<Resturant> findAllByResturantId(String resturantId);

    default Resturant findByResturantId(String resturantId) {
        List<Resturant> list = findAllByResturantId(resturantId);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    List<Resturant> findByResturantNameContainingIgnoreCase(String keyword);
    List<Resturant> findByResturantIdIn(Collection<String> ids);
}

