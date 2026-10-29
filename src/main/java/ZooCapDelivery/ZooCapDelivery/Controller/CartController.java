package ZooCapDelivery.ZooCapDelivery.Controller;

import ZooCapDelivery.ZooCapDelivery.Objects.Cart;
import ZooCapDelivery.ZooCapDelivery.Objects.CartItem;
import ZooCapDelivery.ZooCapDelivery.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping("/addToCart")
     public ResponseEntity addOrder(@RequestBody CartItem cartItem) {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
           String response= cartService.addToCart(cartItem, username);
           if(response.equals("success")) {
               return ResponseEntity.ok().build();
           }else  {
               return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping("/removeItem")
    public ResponseEntity removeItem(@RequestBody CartItem cartItem) {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
           String res = cartService.removeFromCart(cartItem, username);
            if(res=="success") {
                return ResponseEntity.ok().build();
            }
            return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/viewCart")
    public ResponseEntity viewCart() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            var res =  cartService.getCart(username);
            if(res==null) return new  ResponseEntity<>("No Item Added",HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(res,HttpStatus.OK);
        }catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            cartService.clearCart(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
