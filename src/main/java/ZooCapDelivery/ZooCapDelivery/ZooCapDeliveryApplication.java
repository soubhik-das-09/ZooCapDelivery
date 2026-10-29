package ZooCapDelivery.ZooCapDelivery;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class ZooCapDeliveryApplication {

	public static void main(String[] args) {
        SpringApplication.run(ZooCapDeliveryApplication.class, args);
	}
    @Bean
    public PlatformTransactionManager transc(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
//
 }
//@Bean
//    CommandLineRunner testRedis(RedisTemplate<String,String> redisTemplate) {
//        return args -> {
//            redisTemplate.opsForValue().set("test", "hello");
//            System.out.println("Redis Value = " +
//                    redisTemplate.opsForValue().get("test"));
//
//            System.out.println("Keys = " +
//                    redisTemplate.keys("*"));
//
//        };
//        }
