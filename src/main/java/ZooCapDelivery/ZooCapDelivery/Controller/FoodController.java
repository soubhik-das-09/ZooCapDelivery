package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Objects.FoodRequest;
import ZooCapDelivery.ZooCapDelivery.Repository.FoodRepo;
import ZooCapDelivery.ZooCapDelivery.Service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {
    @Autowired
    private FoodService foodService;
    @PostMapping(
            value = "/addFood",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> addFood(
            @ModelAttribute FoodRequest request
    ) {

        boolean response =
                foodService.addFood(request);

        if(response){

            return ResponseEntity.ok(
                    "Food Added Successfully"
            );

        }

        return ResponseEntity.badRequest()
                .body("Food Not Added");

    }
    @GetMapping("/getByCategory")
    public ResponseEntity<?>  getBycategory(@RequestParam String category) {
          var list =  foodService.getFoodBycategory(category);
          if(list.isEmpty()){
              return new ResponseEntity<>("Food not found", HttpStatus.NOT_FOUND);
          }
          return new ResponseEntity<>(list, HttpStatus.OK);
    }
   @GetMapping("/getByName")
    public ResponseEntity<?>  getByName(@RequestParam String name) {
        var list =  foodService.getFoodByName(name);
        if(list.isEmpty()){
            return new ResponseEntity<>("Food not Available", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
   }
   @GetMapping("foodByResturant")
    public ResponseEntity<?>  getByResturant(@RequestParam String resturant) {
      var isPresent=  foodService.getFoodByResturant(resturant);
      if (isPresent!=null){
          return new ResponseEntity<>(isPresent, HttpStatus.OK);
      }
      return new ResponseEntity<>("Food not found With This resturant", HttpStatus.NOT_FOUND);
   }
   @PutMapping("updateStock")
   public ResponseEntity<?>  updateStock(@RequestParam String foodId,@RequestParam int updateStock) {
       Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
       String username = authentication.getName();
       var res = foodService.updateStock(foodId,username, updateStock);
       if(res.equalsIgnoreCase("Food updated")) {
           return ResponseEntity.ok(res);
       }

       return ResponseEntity.badRequest().body(res);
   }
    @PutMapping("updatePrice")
    public ResponseEntity<?>  updatePrice(@RequestParam String foodId,@RequestParam double price) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = foodService.updatePrice(foodId,username, price);
        if(res.equalsIgnoreCase("Food updated")) {
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.badRequest().body(res);
    }
    @GetMapping("/priceFilter")
    public ResponseEntity<?> filterByPrice(
            @RequestParam Double min,
            @RequestParam Double max
    ) {

        return ResponseEntity.ok(
                foodService.filterByPrice(min, max)
        );
    }
    @PutMapping("updateAvailability")
    public ResponseEntity<?>  updateAvailablity(@RequestParam String foodId,@RequestParam Boolean availability) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = foodService.updateAvailability(foodId,availability,username);
        if(res.equalsIgnoreCase("Food updated")) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }
    @GetMapping("/veg")
    public ResponseEntity<?> getVegFood(
            @RequestParam Boolean veg) {

        return ResponseEntity.ok(
                foodService.getFoodByVeg(veg)
        );
    }
    @PutMapping("updateCategory")
    public ResponseEntity<?>  updateACategory(@RequestParam String foodId,@RequestParam String category) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = foodService.updateCategory(foodId,username,category);
        if(res.equalsIgnoreCase("Food updated")) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }
    @PutMapping("/description")
    public ResponseEntity<?>  updateDescription(@RequestParam String foodId, @RequestParam String description) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = foodService.updateDescription(foodId,username,description);
        if(res.equalsIgnoreCase("Food updated")) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }
    @PutMapping(
            value = "/updateFood",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateFood(
            @RequestParam String foodId,
            @ModelAttribute FoodRequest request
    ) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String res = foodService.updateFood(foodId, username, request);
        if(res.equalsIgnoreCase("Food updated") || res.equalsIgnoreCase("success")) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }
    @DeleteMapping("/deleteFood")
    public ResponseEntity<?>  deleteFood(@RequestParam String foodId) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res =  foodService.deleteFood(foodId,username);
        if(res.equalsIgnoreCase("Food updated")) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }
}
