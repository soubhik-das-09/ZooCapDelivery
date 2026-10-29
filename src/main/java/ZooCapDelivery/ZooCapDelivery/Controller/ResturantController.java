package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.EstraService.EmailBody;
import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Repository.FoodRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import ZooCapDelivery.ZooCapDelivery.Service.ResturantService;
import ZooCapDelivery.ZooCapDelivery.Utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/resturants")
public class ResturantController {
    @Autowired
    private ResturantService resturantService;
    @Autowired
    private EmailBody emailBody;
    @Autowired
    private ResturantRepo resturantRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/addResturant")
    private ResponseEntity<?> addResturant(@RequestBody Resturant resturant) {
        try {
            // Hash the password before saving
            if (resturant.getPassword() != null && !resturant.getPassword().isBlank()) {
                resturant.setPassword(passwordEncoder.encode(resturant.getPassword()));
            }
            resturantService.addResturant(resturant);
            emailBody.sendRestaurantRegistrationSuccessMail(resturant);
            return new ResponseEntity<>("ResturantDetails Added", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("While adding Resturant", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /** Restaurant owner login — resturantId + password */
    @PostMapping("/login")
    public ResponseEntity<?> restaurantLogin(@RequestBody Map<String, String> creds) {
        try {
            String resturantId = creds.get("resturantId");
            String password = creds.get("password");
            if (resturantId == null || password == null) {
                return new ResponseEntity<>("resturantId and password are required", HttpStatus.BAD_REQUEST);
            }
            Resturant restaurant = resturantRepo.findByResturantId(resturantId);
            if (restaurant == null) {
                return new ResponseEntity<>("Invalid Restaurant ID or password", HttpStatus.UNAUTHORIZED);
            }
            if (restaurant.getPassword() == null || !passwordEncoder.matches(password, restaurant.getPassword())) {
                return new ResponseEntity<>("Invalid Restaurant ID or password", HttpStatus.UNAUTHORIZED);
            }
            // JWT subject = resturantId so JwtFilter / UserDeatilsService can resolve it
            String token = jwtUtil.generateToken(restaurant.getResturantId());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Restaurant login error", e);
            return new ResponseEntity<>("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Update restaurant profile & location (authenticated — RESTAURANT role) */
    @PutMapping("/updateResturant")
    public ResponseEntity<?> updateResturant(@RequestParam String id, @RequestBody Resturant updated) {
        try {
            String result = resturantService.updateResturant(id, updated);
            if ("success".equals(result)) {
                return new ResponseEntity<>("Restaurant updated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Update restaurant error", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Update restaurant password (authenticated — RESTAURANT role) */
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestParam String id, @RequestBody Map<String, String> body) {
        try {
            String newPassword = body.get("password");
            if (newPassword == null || newPassword.isBlank()) {
                return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
            }
            String hashed = passwordEncoder.encode(newPassword);
            String result = resturantService.updatePassword(id, hashed);
            if ("success".equals(result)) {
                return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Update password error", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getResturants")
    private ResponseEntity<?> getResturants() {
        var resturants = resturantService.getResturants();
        if (!resturants.isEmpty()) {
            return new ResponseEntity<>(resturants, HttpStatus.OK);
        }
        return new ResponseEntity<>("No Resturants Found", HttpStatus.OK);
    }

    @GetMapping("getresturentById")
    private ResponseEntity<?> getResturentById(@RequestParam String id) {
        var resturant = resturantService.getResturantById(id);
        if (resturant != null) {
            return new ResponseEntity<>(resturant, HttpStatus.OK);
        }
        return new ResponseEntity<>("No Resturant Found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("getresturentByName")
    private ResponseEntity<?> getResturentByName(@RequestParam String resturantName) {
        var resturant = resturantService.getResturantByName(resturantName);
        if (resturant != null) {
            return new ResponseEntity<>(resturant, HttpStatus.OK);
        }
        return new ResponseEntity<>("No Resturant Found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/menu")
    private ResponseEntity<?> getMenu(@RequestParam String id) {
        var response = resturantService.getFoodItems(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("No Food Found", HttpStatus.OK);
    }

    @GetMapping("/menuByName")
    private ResponseEntity<?> getMenuByResturentName(@RequestParam String resturantName) {
        var response = resturantService.getMenu(resturantName);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("No Food Found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurant(@RequestParam String keyword) {
        List<Resturant> restaurants = resturantService.searchRestaurant(keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/category")
    public ResponseEntity<?> getRestaurantsByCategory(@RequestParam String category) {
        return ResponseEntity.ok(resturantService.getRestaurantsByCategory(category));
    }
}
