package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.Objects.Cart;
import ZooCapDelivery.ZooCapDelivery.Objects.CartItem;
import ZooCapDelivery.ZooCapDelivery.Objects.FoodItem;
import ZooCapDelivery.ZooCapDelivery.Repository.CartRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.FoodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private FoodRepo foodRepo;

    //public List<CartItem> getCartItems(){}
    public String addToCart(CartItem cartItem, String userName) {
        Cart cart = cartRepo.findByUserName(userName);
        var foodItem = foodRepo.findById(cartItem.getFoodId());

        if (foodItem.isEmpty()) {
            return "Food item not found";
        }
        if (cart == null) {
            cart = new Cart();
            cart.setUserName(userName);
            cart.setTotalAmount(cartItem.getSubTotal());
            cart.addItems(cartItem);
            cart.setResturantId(foodItem.get().getRestaurantId());
            cartRepo.save(cart);
            return "success";
        }
        if (cart.getResturantId().equals(foodItem.get().getRestaurantId())) {
            boolean found = false;
            for (CartItem item : cart.getItems()) {
                if (item.getFoodName().equals(cartItem.getFoodName())) {
                    item.setQuantity(
                            item.getQuantity() + cartItem.getQuantity()
                    );

                    item.setSubTotal(
                            item.getQuantity() * item.getPrice()
                    );

                    found = true;
                    break;
                }
            }
            cart.addToTotal(cartItem.getSubTotal());
            if (!found) cart.addItems(cartItem);
            cartRepo.save(cart);
            return "success";
        } else {
            return "You can order from only one restaurant at a time";
        }

    }

    public String removeFromCart(CartItem cartItem, String userName) {

        Cart cart = cartRepo.findByUserName(userName);

        if (cart == null) {
            return "Cart not found";
        }

        CartItem itemFound = null;

        for (CartItem item : cart.getItems()) {

            if (item.getFoodId().equals(cartItem.getFoodId())) {

                itemFound = item;
                break;
            }
        }

        if (itemFound == null) {
            return "Food item not found";
        }

        double removedAmount =
                itemFound.getPrice() * cartItem.getQuantity();

        if (itemFound.getQuantity() <= cartItem.getQuantity()) {

            cart.getItems().remove(itemFound);

        } else {

            itemFound.setQuantity(
                    itemFound.getQuantity() - cartItem.getQuantity()
            );

            itemFound.setSubTotal(
                    itemFound.getQuantity() * itemFound.getPrice()
            );
        }

        cart.subTotals(removedAmount);

        if (cart.getItems().isEmpty()) {

            cartRepo.delete(cart);

        } else {

            cartRepo.save(cart);
        }
        return "success";
    }
    public List<CartItem> getCartItems(String userName) {
        Cart cart = cartRepo.findByUserName(userName);
        if (cart == null) return null;
        return cart.getItems();
    }
    public Cart getCart(String userName) {
        return cartRepo.findByUserName(userName);
    }
    public void clearCart(String userName) {
        Cart cart = cartRepo.findByUserName(userName);
        if (cart != null) {
            cartRepo.delete(cart);
        }
    }
}
