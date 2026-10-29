package ZooCapDelivery.ZooCapDelivery.Security;

import ZooCapDelivery.ZooCapDelivery.EstraService.RedisService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Data
public class OtpService {

    @Autowired
    private RedisService redisService;

    private final SecureRandom random = new SecureRandom();

    public String generateOtpCode(String email) {

        StringBuilder otp = new StringBuilder();

        for(int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        String otpCode = otp.toString();
        redisService.set(
                email,
                otpCode,
                TimeUnit.MINUTES.toMillis(10)
        );
        return otpCode;
    }
}