package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.Enums.OrderStatus;
import ZooCapDelivery.ZooCapDelivery.Enums.PaymentStatus;
import ZooCapDelivery.ZooCapDelivery.EstraService.DistanceService;
import ZooCapDelivery.ZooCapDelivery.EstraService.EmailBody;
import ZooCapDelivery.ZooCapDelivery.Objects.*;
import ZooCapDelivery.ZooCapDelivery.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private ResturantRepo resturantRepo;
    @Autowired
    private EmailBody emailBody;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private DistanceService distanceService;
    @Autowired
    private DeliveryPartnerRepo deliveryPartnerRepo;
    public String addOrder(
            Cart cart,
            Address address,
            String username,
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {

        Order newOrder = new Order();

        newOrder.setUserName(username);
        newOrder.setItems(cart.getItems());
        newOrder.setTotalAmount(cart.getTotalAmount());
        newOrder.setStatus(OrderStatus.PREPARING);
        newOrder.setRestaurantId(cart.getResturantId());
        newOrder.setOrderTime(LocalDateTime.now());

        // Razorpay Payment Details
        newOrder.setRazorpayOrderId(razorpayOrderId);
        newOrder.setRazorpayPaymentId(razorpayPaymentId);
        newOrder.setRazorpaySignature(razorpaySignature);
        newOrder.setPaymentStatus(PaymentStatus.PAID);

        // Use coordinates provided by the frontend map picker (no geocoding)
        if (address.getLatitude() == null) address.setLatitude(0.0);
        if (address.getLongitude() == null) address.setLongitude(0.0);

        newOrder.setDeliveryAddress(address);

        Resturant restaurant =
                resturantRepo.findByResturantId(
                        cart.getResturantId()
                );

        if (restaurant == null) {
            return "Restaurant not found";
        }

        Double restaurantLat = (restaurant.getResturantAddress() != null && restaurant.getResturantAddress().getLatitude() != null)
                ? restaurant.getResturantAddress().getLatitude()
                : 0.0;

        Double restaurantLon = (restaurant.getResturantAddress() != null && restaurant.getResturantAddress().getLongitude() != null)
                ? restaurant.getResturantAddress().getLongitude()
                : 0.0;

        Double customerLat = address.getLatitude();
        Double customerLon = address.getLongitude();

        DistanceResponse response =
                distanceService.getDistanceAndTime(
                        restaurantLat,
                        restaurantLon,
                        customerLat,
                        customerLon
                );

        // Enforce 50km delivery radius
        if (response.getDistanceKm() > 50.0) {
            return "Delivery not available: your address is " +
                    String.format("%.1f", response.getDistanceKm()) +
                    " km away. We only deliver within 50 km of the restaurant.";
        }

        newOrder.setDistanceKm(
                response.getDistanceKm()
        );

        newOrder.setEstimatedTimeMinutes(
                response.getEstimatedTimeMinutes()
        );

        double fee =
                calculateDeliveryFee(
                        response.getDistanceKm()
                );

        newOrder.setDeliveryFee(fee);

        newOrder.setFinalAmount(
                newOrder.getTotalAmount() + fee
        );

        try {
            DeliveryPartner partner = deliveryPartnerRepo.findFirstByAvailableTrueAndOnlineTrue();
            if (partner != null) {
                newOrder.setDeliverypartnerDetails(List.of(partner.getUsername(), partner.getPhone()));
                partner.setCurrentOrder(newOrder);
                partner.setAvailable(false);
                deliveryPartnerRepo.save(partner);
            } else {
                newOrder.setDeliverypartnerDetails(new java.util.ArrayList<>());
            }
            orderRepo.save(newOrder);

            emailBody.sendNewOrderNotificationMail(
                    newOrder,
                    restaurant.getResturantName(),
                    restaurant.getResturantEmail()
            );

            cartRepo.deleteById(cart.getId());

            return "success";

        } catch (Exception e) {

            return e.getMessage();
        }
    }
        public List<Order> viewOrderDetails(String username) {
            User user = userRepo.findByuserName(username);
            if (user != null && user.getRole().equalsIgnoreCase("resturant")) {
                List<Order> resturantorders = orderRepo.findByRestaurantId(user.getReturantId());
                return resturantorders;
            }
            // Restaurant-direct JWT: username IS the resturantId
            Resturant restaurant = resturantRepo.findByResturantId(username);
            if (restaurant != null) {
                List<Order> resturantorders = orderRepo.findByRestaurantId(username);
                return resturantorders;
            }
            if (user == null) return null;
            List<Order> order = orderRepo.findByUserName(username);
            if(order==null) return null;
            return order;
        }
//        public void  deleteOrder(String orderId) {
//            orderRepo.deleteById(orderId);
//        }
    @Transactional
    public String changeOrderStatus (
            String orderId,
            OrderStatus orderStatus,
            String username) {

        User user = userRepo.findByuserName(username);

        // Restaurant-direct JWT fallback
        if (user == null) {
            Resturant restaurant = resturantRepo.findByResturantId(username);
            if (restaurant != null) {
                User proxy = new User();
                proxy.setUserName(username);
                proxy.setRole("RESTURANT");
                proxy.setReturantId(username);
                user = proxy;
            }
        }

        if (user == null) {
            return "User not found";
        }
        if (!user.getRole().equalsIgnoreCase("resturant") &&
                !user.getRole().equalsIgnoreCase("admin")) {

            return "Only Admin and Restaurant can modify orders";
        }

        Optional<Order> optionalOrder =
                orderRepo.findById(orderId);

        if (optionalOrder.isEmpty()) {
            return "Order not found";
        }
        switch (optionalOrder.get().getStatus()) {

            case PREPARING:
                // Can move to ACCEPTED or CANCELLED
                break;

            case ACCEPTED:
                if(orderStatus == OrderStatus.ACCEPTED){
                    return "Order already accepted";
                }
                break;

            case DELIVERED:
                return "Delivered orders cannot be modified";

            case CANCELLED:
                return "Cancelled orders cannot be modified";
            default:
                break;
        }
        if (optionalOrder.get().getStatus().equals(OrderStatus.DELIVERED)) return "Order Status is DELIVERED";
        Order order = optionalOrder.get();

        if (user.getRole().equalsIgnoreCase("resturant")) {

            if (!user.getReturantId()
                    .equalsIgnoreCase(order.getRestaurantId())) {

                return "You can only modify orders of your restaurant";
            }
        }

        if (orderStatus == OrderStatus.ACCEPTED) {

            for (CartItem item : order.getItems()) {

                FoodItem food =
                        foodRepo.findById(item.getFoodId())
                                .orElse(null);

                if (food == null) {
                    return "Food item not found: "
                            + item.getFoodName();
                }

                if (food.getQuantity() < item.getQuantity()) {
                    return "Insufficient stock for "
                            + item.getFoodName();
                }
            }

            for (CartItem item : order.getItems()) {

                FoodItem food =
                        foodRepo.findById(item.getFoodId())
                                .orElseThrow();

                food.setQuantity(
                        food.getQuantity()
                                - item.getQuantity()
                );

                foodRepo.save(food);
            }
        }

        if (orderStatus == OrderStatus.CANCELLED) {

            User orderUser =
                    userRepo.findByuserName(order.getUserName());

            if (orderUser != null) {

                emailBody.sendOrderCancellationMail(
                        order,
                        orderUser.getEmail()
                );
            }
        }

        order.setStatus(orderStatus);
        orderRepo.save(order);

        return "success";
    }
    public List<Order> userOrders(String username) {
           List<Order> userorders = orderRepo.findByUserName(username);
           if(userorders==null){
               return null;
           }
           return userorders;
    }
    public double calculateDeliveryFee(double distanceKm) {

        if(distanceKm <= 3) {
            return 20;
        }

        if(distanceKm <= 7) {
            return 40;
        }

        if(distanceKm <= 12) {
            return 60;
        }

        return 80;
    }
}
