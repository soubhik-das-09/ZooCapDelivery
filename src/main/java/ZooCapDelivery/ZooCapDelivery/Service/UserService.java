package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.EstraService.EmailBody;
import ZooCapDelivery.ZooCapDelivery.EstraService.RedisService;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Objects.User;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ResturantRepo resturantRepo;
    @Autowired
    private EmailBody emailBody;
    public String saveUser(User user){

        if(userRepo.findByuserName(user.getUserName()) != null){
            return "Username already exists";
        }

        final String PASSWORD_VALIDATION_MESSAGE =
                "Invalid password. Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.";

        if(!isValidPassword(user.getPassword()))
            return PASSWORD_VALIDATION_MESSAGE;
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if(user.getRole().equalsIgnoreCase("resturant")){
                Resturant resturant = resturantRepo.findByResturantId(user.getReturantId());
                if(resturant == null) return "There is not Resturant Registared" +
                        " with this Resturant Id";
                user.setId(user.getId());
                emailBody.sendMailToRestaurantSignup(user);
                userRepo.save(user);
                return  "success";
            } else if(user.getRole().equalsIgnoreCase("user")){
                emailBody.sendmailToSignup(user);
                userRepo.save(user);
                return "success";
            }
        } catch (Exception e) {
            System.err.println(e);
            return e.getMessage();
        }
        return "BAD REQUEST";
    }
    public ResponseEntity saveAdminUser(User user){
        try {
                user.setRole("ADMIN");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepo.save(user);
        } catch (Exception e) {
            System.err.println(e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return  new ResponseEntity<>(HttpStatus.OK);
    }
    public User getUser(String username){
        User user = userRepo.findByuserName(username);
        if(user == null){
            user = userRepo.findByEmail(username);
        }
        if(user == null){
            user = userRepo.findByPhNumber(username);
        }
        return user;
    }
    public Boolean resetPasword(String newPassword,User user,String otp){
        Boolean verify = verifyOtp(user.getEmail(),otp);
        if(verify){
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return true;
        }
        return false;
    }
    public boolean verifyOtp(String email, String userOtp) {
        String storedOtp = redisService.get(email);
        System.out.println(storedOtp);
        if (storedOtp == null) {
            return false;
        }
        if (storedOtp.equals(userOtp)) {
            redisService.delete(email);
            return true;
        }
        return false;
    }
    public boolean isValidPassword(String password){

        String regex =
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        return password.matches(regex);
    }
}
