package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.Enums.OrderStatus;
import ZooCapDelivery.ZooCapDelivery.Objects.CheckoutResponse;
import ZooCapDelivery.ZooCapDelivery.Objects.VerifyPaymentRequest;
import ZooCapDelivery.ZooCapDelivery.Service.OrderService;
import ZooCapDelivery.ZooCapDelivery.EstraService.PaymentService;
import ZooCapDelivery.ZooCapDelivery.Objects.Address;
import ZooCapDelivery.ZooCapDelivery.Objects.Cart;
import ZooCapDelivery.ZooCapDelivery.Repository.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Address address) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Cart cart = cartRepo.findByUserName(username);

        if (cart == null) {
            return ResponseEntity.badRequest()
                    .body("Cart is empty");
        }

        try {

            CheckoutResponse response =
                    paymentService.createPayment(
                            cart,
                            address,
                            username
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody VerifyPaymentRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        try {

            String result =
                    paymentService.verifyPayment(
                            request,
                            username
                    );

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @GetMapping("/viewOrderDetails")
    public ResponseEntity viewOrderDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res =  orderService.viewOrderDetails(username);
        if(res!=null) return new ResponseEntity(res,HttpStatus.OK);
        return new ResponseEntity("There is not order with this username or Id",HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/resturantOrders")
    public ResponseEntity resturantOrders( @RequestParam String orderId ,@RequestParam OrderStatus status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String res =orderService.changeOrderStatus(orderId,status,username);
        if(res.equalsIgnoreCase("success")) return new ResponseEntity(res,HttpStatus.OK);
        else return new ResponseEntity(res,HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/myOrders")
    public ResponseEntity getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var res = orderService.userOrders(username);
        if(res!=null) return new ResponseEntity(res,HttpStatus.OK);
        return new ResponseEntity("No orders Found",HttpStatus.BAD_REQUEST);
    }

}
