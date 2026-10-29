package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.Objects.CheckoutResponse;
import ZooCapDelivery.ZooCapDelivery.Objects.VerifyPaymentRequest;
import ZooCapDelivery.ZooCapDelivery.Objects.Address;
import ZooCapDelivery.ZooCapDelivery.Objects.Cart;
import ZooCapDelivery.ZooCapDelivery.Repository.CartRepo;
import ZooCapDelivery.ZooCapDelivery.EstraService.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartRepo cartRepo;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody Address address) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Cart cart = cartRepo.findByUserName(username);

        if(cart == null){

            return ResponseEntity.badRequest()
                    .body("Cart is empty");
        }

        try{

            CheckoutResponse response =
                    paymentService.createPayment(
                            cart,
                            address,
                            username
                    );

            return ResponseEntity.ok(response);

        }catch (Exception e){

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody VerifyPaymentRequest request){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username =
                authentication.getName();

        try{

            String response =
                    paymentService.verifyPayment(
                            request,
                            username
                    );

            return ResponseEntity.ok(response);

        }catch (Exception e){

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }

}