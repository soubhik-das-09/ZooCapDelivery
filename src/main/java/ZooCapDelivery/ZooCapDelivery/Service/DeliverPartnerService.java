package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.Enums.OrderStatus;
import ZooCapDelivery.ZooCapDelivery.EstraService.EmailBody;
import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import ZooCapDelivery.ZooCapDelivery.Objects.Order;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Objects.User;
import ZooCapDelivery.ZooCapDelivery.Repository.DeliveryPartnerRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.OrderRepo;
import ZooCapDelivery.ZooCapDelivery.Utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class DeliverPartnerService {
    @Autowired
    private DeliveryPartnerRepo deliveryPartnerRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailBody  emailBody;
    @Autowired
    private OrderRepo orderRepo;
    public String  addDeliverPartner( DeliveryPartner deliveryPartner){

        DeliveryPartner partner = deliveryPartnerRepo.findDeliveryPartnerById(deliveryPartner.getDeliveryPartnerId());
        if(partner!=null) return "Delivery Partner already exists";
        final String PASSWORD_VALIDATION_MESSAGE =
                "Invalid password. Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.";

        if(!isValidPassword(deliveryPartner.getPassword()))
            return PASSWORD_VALIDATION_MESSAGE;
        try {
            deliveryPartner.setPassword(passwordEncoder.encode(deliveryPartner.getPassword()));
            deliveryPartner.setCurrentOrder(null);
                deliveryPartnerRepo.save(deliveryPartner);
                emailBody.sendMailToDeliveryPartner(deliveryPartner);
                return  "success";

        } catch (Exception e) {
            System.err.println(e);
            return e.getMessage();
        }
    }
    public String  loginPartner(DeliveryPartner deliveryPartner){
        try {

            DeliveryPartner dbUser = deliveryPartnerRepo.findDeliveryPartnerByUsername(deliveryPartner.getUsername());
            if (dbUser == null) {
                log.info("Partner not found: {}", deliveryPartner.getUsername());
                return null;
            }
            System.out.println(
                    "MATCHES = " +
                            passwordEncoder.matches(
                                    deliveryPartner.getPassword(),
                                    dbUser.getPassword()
                            )
            );
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(deliveryPartner.getUsername(), deliveryPartner.getPassword()));
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            System.out.println("Authentication Success");
            String jwt =jwtUtil.generateToken(deliveryPartner.getUsername());
            return jwt;
        }catch (Exception ex){
            log.error("Error While trying to log in",ex);
            System.err.println(ex.getMessage());
            return null;
        }
    }
    public DeliveryPartner findDeliveryPartnerByName(String username){
        DeliveryPartner partner =deliveryPartnerRepo.findDeliveryPartnerByUsername(username);
         return partner;
    }
//    public DeliveryPartner viewProfile(String username){
//        return deliveryPartnerRepo.findDeliveryPartnerByName(username);
//    }
    public boolean isValidPassword(String password){

        String regex =
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        return password.matches(regex);
    }
    public boolean updateAvailability(String userName , Boolean availability){
        DeliveryPartner  deliveryPartner =deliveryPartnerRepo.findDeliveryPartnerByUsername(userName);
        try{
            deliveryPartner.setOnline(availability);
            deliveryPartnerRepo.save(deliveryPartner);
            return true;
        }catch (Exception ex){
            log.error(ex.getMessage());
            System.err.println(ex.getMessage());
            return false;
        }

    }
    public boolean changeOrderStatus(String userName, String status){
        DeliveryPartner  deliveryPartner =deliveryPartnerRepo.findDeliveryPartnerByUsername(userName);
        if(deliveryPartner == null) return false;
        if(deliveryPartner.getCurrentOrder()==null) return false;
        Order order = orderRepo.findByOrderId(deliveryPartner.getCurrentOrder().getOrderId());
        if(order==null) return false;
        if(status.equalsIgnoreCase("delivered")){
            try {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepo.save(order);
                deliveryPartner.setCurrentOrder(null);
                deliveryPartner.setAvailable(true);
                deliveryPartner.setTotalDeliveries(
                        deliveryPartner.getTotalDeliveries()+1
                );
                double earning =
                        calculateDeliveryPartnerEarning(order.getDistanceKm());
                deliveryPartner.setTodaysEarning(
                        deliveryPartner.getTodaysEarning() + earning
                );
                deliveryPartner.setTotalEarnings(
                        deliveryPartner.getTotalEarnings() + earning
                );
                deliveryPartnerRepo.save(deliveryPartner);

                return true;
            }catch (Exception ex){
                log.error(ex.getMessage());
                System.err.println(ex.getMessage());
                return false;
            }
        } else if(status.equalsIgnoreCase("out_for_delivery") || status.equalsIgnoreCase("OUT_FOR_DELIVERY")){
            try {
                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                orderRepo.save(order);
                deliveryPartner.setAvailable(false);
                deliveryPartnerRepo.save(deliveryPartner);
                return true;
            } catch (Exception ex){
                log.error(ex.getMessage());
                return false;
            }
        }
        return false;
    }
    public double calculateDeliveryPartnerEarning(double distanceKm) {

        if (distanceKm <= 3)
            return 20;

        if (distanceKm <= 7)
            return 35;

        if (distanceKm <= 12)
            return 50;

        return 70;
    }
    
    public java.util.List<Order> getPartnerOrders(String username) {
        return orderRepo.findByDeliverypartnerDetails(username);
    }

    @org.springframework.transaction.annotation.Transactional
    public String acceptOrder(String userName, String orderId) {
        DeliveryPartner deliveryPartner = deliveryPartnerRepo.findDeliveryPartnerByUsername(userName);
        if (deliveryPartner == null) return "Delivery Partner not found";
        if (deliveryPartner.getCurrentOrder() != null) return "Delivery Partner already has an active order";
        
        Order order = orderRepo.findByOrderId(orderId);
        if (order == null) return "Order not found";
        if (order.getDeliverypartnerDetails() != null && !order.getDeliverypartnerDetails().isEmpty()) {
            return "Order already accepted by another driver";
        }
        
        order.setDeliverypartnerDetails(java.util.List.of(deliveryPartner.getUsername(), deliveryPartner.getPhone()));
        deliveryPartner.setCurrentOrder(order);
        deliveryPartner.setAvailable(false);
        
        orderRepo.save(order);
        deliveryPartnerRepo.save(deliveryPartner);
        return "success";
    }

    @org.springframework.transaction.annotation.Transactional
    public String rejectOrder(String userName, String orderId) {
        DeliveryPartner deliveryPartner = deliveryPartnerRepo.findDeliveryPartnerByUsername(userName);
        if (deliveryPartner == null) return "Delivery Partner not found";
        if (deliveryPartner.getCurrentOrder() == null || !deliveryPartner.getCurrentOrder().getOrderId().equals(orderId)) {
            return "This order is not assigned to you";
        }
        
        Order order = orderRepo.findByOrderId(orderId);
        if (order != null) {
            order.setDeliverypartnerDetails(new java.util.ArrayList<>());
            orderRepo.save(order);
        }
        
        deliveryPartner.setCurrentOrder(null);
        deliveryPartner.setAvailable(true);
        deliveryPartnerRepo.save(deliveryPartner);
        return "success";
    }

    public java.util.List<Order> getAvailableOrders() {
        return orderRepo.findUnassignedOrders();
    }

    public boolean updateLocation(String userName, double lat, double lon) {
        DeliveryPartner deliveryPartner = deliveryPartnerRepo.findDeliveryPartnerByUsername(userName);
        if (deliveryPartner == null) return false;
        deliveryPartner.setLatitude(lat);
        deliveryPartner.setLongitude(lon);
        deliveryPartnerRepo.save(deliveryPartner);
        return true;
    }
}
