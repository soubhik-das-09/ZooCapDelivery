package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.EstraService.GeoCodingService;
import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Repository.FoodRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResturantService {
    @Autowired
    private ResturantRepo resturantRepo;
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private GeoCodingService geoCodingService;

    public void addResturant(Resturant resturant) {
        resturant.setResturantAddress(
                geoCodingService.populateCoordinates(
                        resturant.getResturantAddress()
                )
        );
        System.out.println(resturant.getResturantAddress());
        resturantRepo.save(resturant);
    }

    /** Update profile and/or location of an existing restaurant */
    public String updateResturant(String resturantId, Resturant updated) {
        Resturant existing = resturantRepo.findByResturantId(resturantId);
        if (existing == null) return "Restaurant not found";

        if (updated.getResturantName() != null && !updated.getResturantName().isBlank())
            existing.setResturantName(updated.getResturantName());
        if (updated.getResturantPhone() != null && !updated.getResturantPhone().isBlank())
            existing.setResturantPhone(updated.getResturantPhone());
        if (updated.getResturantEmail() != null && !updated.getResturantEmail().isBlank())
            existing.setResturantEmail(updated.getResturantEmail());
        if (updated.getRating() != null)
            existing.setRating(updated.getRating());
        existing.setOpen(updated.isOpen());

        if (updated.getResturantAddress() != null) {
            existing.setResturantAddress(
                    geoCodingService.populateCoordinates(updated.getResturantAddress())
            );
        }
        resturantRepo.save(existing);
        return "success";
    }

    /** Update just the password of a restaurant */
    public String updatePassword(String resturantId, String hashedPassword) {
        Resturant existing = resturantRepo.findByResturantId(resturantId);
        if (existing == null) return "Restaurant not found";
        existing.setPassword(hashedPassword);
        resturantRepo.save(existing);
        return "success";
    }

    private void populateMenu(Resturant r) {
        if (r != null) {
            r.setOpen(true);
            if (r.getResturantId() != null) {
                r.setMenu(foodRepo.findByRestaurantId(r.getResturantId()));
            }
        }
    }

    public List<Resturant> getResturants() {
        var result = resturantRepo.findAll();
        if (result != null) {
            for (Resturant r : result) {
                populateMenu(r);
            }
        }
        return result;
    }

    public Resturant getResturantById(String id) {
        var result = resturantRepo.findByResturantId(id);
        populateMenu(result);
        return result;
    }

    public Resturant getResturantByName(String id) {
        var result = resturantRepo.findByResturantName(id);
        populateMenu(result);
        return result;
    }

    public List<FoodItem> getFoodItems(String restaurantId) {
        var list = foodRepo.findByRestaurantId(restaurantId);
        if (list != null) {
            return list;
        }
        return null;
    }

    public List<FoodItem> getMenu(String restaurantName) {
        var restaurant = resturantRepo.findByResturantName(restaurantName);
        if (restaurant == null) {
            return Collections.emptyList();
        }
        return foodRepo.findByRestaurantId(restaurant.getResturantId());
    }

    public List<Resturant> searchRestaurant(String keyword) {
        List<Resturant> result;
        if (keyword == null || keyword.trim().isEmpty()) {
            result = resturantRepo.findAll();
        } else {
            result = resturantRepo.findByResturantNameContainingIgnoreCase(keyword);
        }
        if (result != null) {
            for (Resturant r : result) {
                populateMenu(r);
            }
        }
        return result;
    }

    public List<Resturant> getRestaurantsByCategory(String category) {
        List<FoodItem> foods = foodRepo.findByCategoryIgnoreCase(category);
        Set<String> restaurantIds = new HashSet<>();
        for (FoodItem food : foods) {
            restaurantIds.add(food.getRestaurantId());
        }
        List<Resturant> result = resturantRepo.findByResturantIdIn(restaurantIds);
        if (result != null) {
            for (Resturant r : result) {
                populateMenu(r);
            }
        }
        return result;
    }
}
