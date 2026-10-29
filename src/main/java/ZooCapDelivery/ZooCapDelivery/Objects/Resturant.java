package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Resturants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resturant {
    @NonNull
    @Indexed(unique = true)
    String resturantId;
    String resturantName;
    Address resturantAddress;
    String resturantPhone;
    String resturantEmail;
    String rating;
    boolean isOpen;
    String password;
    private List<FoodItem> menu;
    public void addMenu(FoodItem item){
        if(menu == null){
            menu = new ArrayList<>();
        }
        menu.add(item);
    }
    public List<FoodItem> getMenu(){
        return this.menu;
    }
}
