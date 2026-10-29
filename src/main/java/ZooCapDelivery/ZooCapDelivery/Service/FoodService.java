package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.EstraService.S3Service;
import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Objects.*;
import ZooCapDelivery.ZooCapDelivery.Repository.FoodRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FoodService {
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private ResturantRepo resturantRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private S3Service s3Service;
    @Transactional
    public Boolean addFood(FoodRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username =
                authentication.getName();

        // Try to find as a normal user first
        User user = userRepo.findByuserName(username);

        String resolvedResturantId = null;
        Resturant restaurant = null;

        if (user != null) {
            // Regular user (restaurant owner linked via User record)
            resolvedResturantId = user.getReturantId();
            if (resolvedResturantId == null) return false;
            restaurant = resturantRepo.findByResturantId(resolvedResturantId);
        } else {
            // Restaurant owner logged in directly with resturantId as JWT subject
            restaurant = resturantRepo.findByResturantId(username);
            if (restaurant != null) {
                resolvedResturantId = username;
            }
        }

        if (restaurant == null || resolvedResturantId == null) {
            return false;
        }

        try {

            FoodItem foodItem =
                    new FoodItem();

            foodItem.setRestaurantId(resolvedResturantId);

            foodItem.setName(
                    request.getName()
            );

            foodItem.setPrice(
                    request.getPrice()
            );

            foodItem.setCategory(
                    request.getCategory()
            );

            foodItem.setAvailable(
                    request.getAvailable()
            );

            foodItem.setDescription(
                    request.getDescription()
            );

            foodItem.setQuantity(
                    request.getQuantity()
            );

            foodItem.setVeg(
                    request.getVeg()
            );

            List<String> urls =
                    new ArrayList<>();

            if (request.getImages() != null) {
                for (MultipartFile image : request.getImages()) {
                    if (image != null && !image.isEmpty()) {
                        urls.add(
                                s3Service.uploadImage(image)
                        );
                    }
                }
            }

            foodItem.setImage(urls);

            foodRepo.save(foodItem);

            restaurant.addMenu(foodItem);

            resturantRepo.save(restaurant);

            return true;

        }

        catch (Exception e){

            e.printStackTrace();

            return false;

        }

    }
    /**
     * Resolves a User-like wrapper from the JWT subject.
     * Works for both normal User accounts AND restaurant-direct login (resturantId as subject).
     * Returns null if neither is found.
     */
    private User resolveUser(String username) {
        User user = userRepo.findByuserName(username);
        if (user != null) return user;
        // Try restaurant-direct login: JWT subject == resturantId
        Resturant restaurant = resturantRepo.findByResturantId(username);
        if (restaurant != null) {
            User proxy = new User();
            proxy.setUserName(username);
            proxy.setRole("RESTURANT");
            proxy.setReturantId(username);
            return proxy;
        }
        return null;
    }

    public List<FoodItem> getFoodBycategory(String category){
      var list = foodRepo.findByCategory(category);
      if(!list.isEmpty()){
          return list;
      }
      return null;
    }
    public List<FoodItem> filterByPrice(
            Double min,
            Double max
    ) {
        return foodRepo.findByPriceBetween(min, max);
    }
    public List<FoodItem> getFoodByVeg(Boolean veg) {
        return foodRepo.findByVeg(veg);
    }
    public List<FoodItem> getFoodByName(String item){
        var list = foodRepo.findAll();
        if(!list.isEmpty()){
            List<FoodItem> food = list.stream()
                    .filter(f ->
                            f.getName()
                                    .toLowerCase()
                                    .contains(item.toLowerCase())).toList();
            return food;
        }
        return null;
    }
    public List<FoodItem> getFoodByResturant(String resturant){
        Resturant resturnat = resturantRepo.findByResturantId(resturant);
        if (resturnat == null) {
            resturnat = resturantRepo.findByResturantName(resturant);
        }
        if(resturnat != null){
            String id = resturnat.getResturantId();
            if(id==null){
                return null;
            }
            var  list = foodRepo.findByRestaurantId(id);
            if(!list.isEmpty()){
                return list;
            }
        }
        return null;
    }
    public String updateStock(String foodId,
                              String userName,
                              int quantity) {

        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can make changes";
        }

        if(quantity < 0) {
            return "Quantity cannot be negative";
        }

        food.get().setQuantity(quantity);

        try {
            foodRepo.save(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food Stock", e);
            return e.getMessage();
        }
    }


    public String updatePrice(String foodId,
                              String userName,
                              double price) {

        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can make changes";
        }

        if(price < 0) {
            return "Price cannot be negative";
        }

        food.get().setPrice(price);
        try {
            foodRepo.save(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food price", e);
            return e.getMessage();
        }
    }
    public String updateAvailability(String foodId,Boolean availability,String userName) {

        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can make changes";
        }
        food.get().setAvailable(availability);
        try {
            foodRepo.save(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food price", e);
            return e.getMessage();
        }
    }
    public String deleteFood(String foodId,String userName) {
        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can Delete Foods";
        }
        try {
            foodRepo.delete(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error Deleting food named and id is: "+
                    food.get().getName() +"and" + food.get().getId(), e);
            return e.getMessage();
        }
    }
    public String updateCategory(String foodId,
                              String userName,
                              String  category) {

        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can make changes";
        }

        food.get().setCategory(category);
        try {
            foodRepo.save(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food price", e);
            return e.getMessage();
        }

    }
    public String updateDescription(String foodId,String userName,String description) {
        User user = resolveUser(userName);
        if(user == null) {
            return "User not found";
        }

        if(!user.getRole().equalsIgnoreCase("resturant")
                &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Restaurant Owner can make changes";
        }

        var food = foodRepo.findById(foodId);

        if(food.isEmpty()) {
            return "Food not found";
        }

        if(!user.getRole().equalsIgnoreCase("admin")
                &&
                !food.get().getRestaurantId()
                        .equals(user.getReturantId())) {

            return "Only Restaurant Owner of that food can make changes";
        }

        food.get().setDescription(description);
        try {
            foodRepo.save(food.get());
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food price", e);
            return e.getMessage();
        }
    }

    @Transactional
    public String updateFood(String foodId, String userName, FoodRequest request) {
        User user = resolveUser(userName);
        if (user == null) {
            return "User not found";
        }
        if (!user.getRole().equalsIgnoreCase("resturant") && !user.getRole().equalsIgnoreCase("admin")) {
            return "Only Restaurant Owner can make changes";
        }
        Optional<FoodItem> optFood = foodRepo.findById(foodId);
        if (optFood.isEmpty()) {
            return "Food not found";
        }
        FoodItem food = optFood.get();
        if (!user.getRole().equalsIgnoreCase("admin") && !food.getRestaurantId().equals(user.getReturantId())) {
            return "Only Restaurant Owner of that food can make changes";
        }

        if (request.getName() != null) {
            food.setName(request.getName());
        }
        if (request.getPrice() != null) {
            if (request.getPrice() < 0) return "Price cannot be negative";
            food.setPrice(request.getPrice());
        }
        if (request.getCategory() != null) {
            food.setCategory(request.getCategory());
        }
        if (request.getAvailable() != null) {
            food.setAvailable(request.getAvailable());
        }
        if (request.getDescription() != null) {
            food.setDescription(request.getDescription());
        }
        if (request.getQuantity() != null) {
            if (request.getQuantity() < 0) return "Quantity cannot be negative";
            food.setQuantity(request.getQuantity());
        }
        if (request.getVeg() != null) {
            food.setVeg(request.getVeg());
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                List<String> urls = new java.util.ArrayList<>();
                for (MultipartFile image : request.getImages()) {
                    urls.add(s3Service.uploadImage(image));
                }
                food.setImage(urls);
            } catch (Exception e) {
                log.error("Error uploading images", e);
                return "Error uploading images: " + e.getMessage();
            }
        }

        try {
            foodRepo.save(food);
            Resturant restaurant = resturantRepo.findByResturantId(food.getRestaurantId());
            if (restaurant != null) {
                List<FoodItem> menu = restaurant.getMenu();
                if (menu != null) {
                    for (int i = 0; i < menu.size(); i++) {
                        if (menu.get(i).getId().equals(food.getId())) {
                            menu.set(i, food);
                            break;
                        }
                    }
                    resturantRepo.save(restaurant);
                }
            }
            return "Food updated";
        } catch (Exception e) {
            log.error("Error updating food details", e);
            return e.getMessage();
        }
    }
}
