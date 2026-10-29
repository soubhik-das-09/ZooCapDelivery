package ZooCapDelivery.ZooCapDelivery.Repository;

import ZooCapDelivery.ZooCapDelivery.Objects.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User,String> {
    User findFirstByuserName(String username);
    User findFirstByEmail(String email);
    User findFirstByPhNumber(String phNumber);

    default User findByuserName(String username) {
        return findFirstByuserName(username);
    }
    default User findByEmail(String email) {
        return findFirstByEmail(email);
    }
    default User findByPhNumber(String phNumber) {
        return findFirstByPhNumber(phNumber);
    }
}
