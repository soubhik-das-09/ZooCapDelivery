package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import ZooCapDelivery.ZooCapDelivery.Service.DeliverPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryPartner")
public class DeliveryPartnerController {
    @Autowired
    private DeliverPartnerService deliverPartnerService;
    @PostMapping("/register")
    public ResponseEntity registerPartner(@RequestBody DeliveryPartner deliveryPartner){
              String response = deliverPartnerService.addDeliverPartner(deliveryPartner);
              if(response.contains("success")) return ResponseEntity.ok().body(response);
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @GetMapping("/find")
    public ResponseEntity findDeliveryPartnerByName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        DeliveryPartner deliveryPartner =deliverPartnerService.findDeliveryPartnerByName(username);
        if(deliveryPartner!=null) return ResponseEntity.ok().body(deliveryPartner);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delivery Partner found");
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody DeliveryPartner deliveryPartner){
        String response = deliverPartnerService.loginPartner(deliveryPartner);
        if(response!=null) return ResponseEntity.ok().body(response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incoreect Username and Password");
    }
    @GetMapping("/viewProfile")
    public ResponseEntity viewProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        DeliveryPartner response =deliverPartnerService.findDeliveryPartnerByName(username);
        if(response!=null) return ResponseEntity.ok().body(response);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delivery Partner not found");
    }
    @PutMapping("/updateOnline")
    public ResponseEntity updateStatus(@RequestParam Boolean availability){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
      var available =  deliverPartnerService.updateAvailability(username, availability);
        if(available) return ResponseEntity.ok().body("Update Succesfull");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Use Update Failed");
    }
//    @PutMapping("/updateAvailability")
//    public ResponseEntity updateStatus(@RequestParam Boolean availability){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        var available =  deliverPartnerService.updateAvailability(username, availability);
//        if(available) return ResponseEntity.ok().body("Udate Succesfull");
//        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Use Update Failed");
//    }

    @PutMapping("/changeOrderStatus")
    public ResponseEntity changeOrderStatus(@RequestParam String status){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = deliverPartnerService.changeOrderStatus(username, status);
        if (res) return ResponseEntity.ok().body("Update Succesfull");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" Update Failed");
    }
    
    @GetMapping("/orders")
    public ResponseEntity getOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(deliverPartnerService.getPartnerOrders(username));
    }

    @PostMapping("/acceptOrder")
    public ResponseEntity acceptOrder(@RequestParam String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String res = deliverPartnerService.acceptOrder(username, orderId);
        if (res.equals("success")) {
            return ResponseEntity.ok().body("Order accepted successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @PostMapping("/rejectOrder")
    public ResponseEntity rejectOrder(@RequestParam String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String res = deliverPartnerService.rejectOrder(username, orderId);
        if (res.equals("success")) {
            return ResponseEntity.ok().body("Order rejected successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @GetMapping("/availableOrders")
    public ResponseEntity getAvailableOrders() {
        return ResponseEntity.ok(deliverPartnerService.getAvailableOrders());
    }

    @PutMapping("/location")
    public ResponseEntity updateLocation(@RequestParam double latitude, @RequestParam double longitude) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean res = deliverPartnerService.updateLocation(username, latitude, longitude);
        if (res) {
            return ResponseEntity.ok().body("Location updated successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update location");
    }
}
