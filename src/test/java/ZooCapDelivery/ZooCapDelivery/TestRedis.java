//package ZooCapDelivery.ZooCapDelivery;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//@Configuration
//public class TestRedis {
//    @Bean
//    CommandLineRunner testRedis(RedisTemplate<String,String> redisTemplate) {
//        return args -> {
//            redisTemplate.opsForValue().set("test", "hello");
//            System.out.println(redisTemplate.opsForValue().get("test"));
//        };
//    }
//}
