package ZooCapDelivery.ZooCapDelivery.EstraService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value, long minutes) {
        redisTemplate.opsForValue()
                .set(key, value, minutes, TimeUnit.MINUTES);
    }

    public String get(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error while fetching from Redis", e);
            return null;
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

