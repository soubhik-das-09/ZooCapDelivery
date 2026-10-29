package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.EstraService.EmailBody;
import ZooCapDelivery.ZooCapDelivery.Objects.OtpClass;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Objects.User;
import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import ZooCapDelivery.ZooCapDelivery.Repository.DeliveryPartnerRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import ZooCapDelivery.ZooCapDelivery.Security.OtpService;
import ZooCapDelivery.ZooCapDelivery.Service.UserService;
import ZooCapDelivery.ZooCapDelivery.Utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailBody emailBody;
    @Autowired
    private OtpService otpService;
    @Autowired
    private DeliveryPartnerRepo deliveryPartnerRepo;
    @Autowired
    private ResturantRepo resturantRepo;
    
    @GetMapping("/health")
    public ResponseEntity healthcheck(){
        System.out.println("hello All ok Ready to go!!");
        return new ResponseEntity<>("ALl good Ready to go",HttpStatus.OK);
    }
    @PostMapping("/signup")
    private ResponseEntity userSignup(@RequestBody User user
       ){
        try {
            var res = userService.saveUser(user);
            if (!res.equals("success")) return  new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(res,HttpStatus.OK);
        }catch (Exception e){
            System.err.println(e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/adminSignup")
    private ResponseEntity adminignUp(@RequestBody User user
    ){
        try {
            var res = userService.saveAdminUser(user);
            emailBody.sendmailToSignup(user);
            return res;
        }catch (Exception e){
            System.err.println(e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody User user
    ){
        try {
            System.out.println("Login Started");
            User dbUser = userService.getUser(user.getUserName());
            if (dbUser == null) {
                return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
            }
               System.out.println("INPUT PASSWORD = " + user.getPassword());
               System.out.println("DB PASSWORD = " + dbUser.getPassword());

               System.out.println(
                       "MATCHES = " +
                               passwordEncoder.matches(
                                       user.getPassword(),
                                       dbUser.getPassword()
                               )
               );
               authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            System.out.println("Authentication Success");
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            System.out.println("Request Password = " + user.getPassword());
            System.out.println("DB Password = " + userDetails.getPassword());
            String jwt =jwtUtil.generateToken(user.getUserName());
            User userToSendMail = userService.getUser(user.getUserName());
            emailBody.sendmailTologin(userToSendMail);
            return new ResponseEntity<>(jwt,HttpStatus.OK);
        }catch (Exception ex){
            log.error("Error While trying to log in",ex);
            System.err.println(ex.getMessage());
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody OtpClass otpClass) {
         try {
             User user = userService.getUser(otpClass.getUserName());

             if (user == null) {
                 return new ResponseEntity<>("User does not exist",
                         HttpStatus.NOT_FOUND);
             }

             String otp = otpService.generateOtpCode(user.getEmail());
             emailBody.sendmailToForgotPassword(user, otp);
             System.out.println("Otp sent to : " + user.getEmail());
             return new ResponseEntity<>("OTP sent to your email",
                     HttpStatus.OK);
         }catch (Exception ex){
             return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }
    @PostMapping("/password/otp/{otp}")
    public ResponseEntity<?> resetPassword(@RequestBody User user, @PathVariable String otp) {
         User userDetails = userService.getUser(user.getUserName());
        if(userDetails!=null){
            Boolean otpResponse = userService.resetPasword(user.getPassword(),userDetails,otp);
            if (otpResponse) {
                emailBody.sendPasswordResetSuccessMail(userDetails);
                return new ResponseEntity<>("PassWord Reset Done!",HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Password Reset Failed!",HttpStatus.NOT_MODIFIED);
            }
        }
        return new ResponseEntity<>("Otp Expired or Wrong Otp",HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUser(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        }

        DeliveryPartner partner = deliveryPartnerRepo.findDeliveryPartnerByName(username);
        if (partner != null) {
            User mappedUser = new User();
            mappedUser.setUserName(partner.getUsername());
            mappedUser.setPassword(partner.getPassword());
            mappedUser.setEmail(partner.getEmail());
            mappedUser.setPhNumber(partner.getPhone());
            mappedUser.setRole("DELIVERY");
            return ResponseEntity.ok(mappedUser);
        }

        // Restaurant owner: JWT subject is resturantId — look up in Resturant collection
        Resturant restaurant = resturantRepo.findByResturantId(username);
        if (restaurant != null) {
            User mappedUser = new User();
            mappedUser.setUserName(restaurant.getResturantId());
            mappedUser.setEmail(restaurant.getResturantEmail());
            mappedUser.setPhNumber(restaurant.getResturantPhone());
            mappedUser.setRole("RESTAURANT");
            mappedUser.setReturantId(restaurant.getResturantId());
            return ResponseEntity.ok(mappedUser);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}
