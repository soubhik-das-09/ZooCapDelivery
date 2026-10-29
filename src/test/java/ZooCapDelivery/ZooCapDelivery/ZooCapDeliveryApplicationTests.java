package ZooCapDelivery.ZooCapDelivery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ZooCapDeliveryApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(ZooCapDeliveryApplicationTests.class, args);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void printDbCollections() {
        System.out.println("==================================================");
        System.out.println("DIAGNOSTIC DATABASE INSPECTOR START");
        
        System.out.println("\n--- COLLECTION: DeliveryPartners ---");
        try {
            var partners = mongoTemplate.findAll(ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner.class);
            for (var p : partners) {
                System.out.printf("Partner -> id: %s, username: %s, name: %s, online: %b, available: %b\n",
                        p.getDeliveryPartnerId(), p.getUsername(), p.getName(), p.isOnline(), p.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error reading DeliveryPartners: " + e.getMessage());
        }

        System.out.println("\n--- COLLECTION: Resturants ---");
        try {
            var restaurants = mongoTemplate.findAll(ZooCapDelivery.ZooCapDelivery.Objects.Resturant.class);
            for (var r : restaurants) {
                System.out.printf("Restaurant -> id: %s, name: %s, isOpen: %b, menuSize: %d\n",
                        r.getResturantId(), r.getResturantName(), r.isOpen(), r.getMenu() != null ? r.getMenu().size() : 0);
            }
        } catch (Exception e) {
            System.out.println("Error reading Restaurants: " + e.getMessage());
        }

        System.out.println("\n--- COLLECTION: FoodItem ---");
        try {
            var foods = mongoTemplate.findAll(ZooCapDelivery.ZooCapDelivery.Objects.FoodItem.class);
            for (var f : foods) {
                System.out.printf("FoodItem -> id: %s, name: %s, restaurantId: %s, category: %s, available: %b, images: %s\n",
                        f.getId(), f.getName(), f.getRestaurantId(), f.getCategory(), f.getAvailable(), f.getImage());
            }
        } catch (Exception e) {
            System.out.println("Error reading FoodItem: " + e.getMessage());
        }

        System.out.println("DIAGNOSTIC DATABASE INSPECTOR END");
        System.out.println("==================================================");
    }

    @Bean
    CommandLineRunner testRedis(RedisTemplate<String,String> redisTemplate) {
        return args -> {
            redisTemplate.opsForValue().set("test", "hello");
            System.out.println(redisTemplate.opsForValue().get("test"));
        };
    }

}
