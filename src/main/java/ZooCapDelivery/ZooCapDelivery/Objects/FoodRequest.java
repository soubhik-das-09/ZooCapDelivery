package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FoodRequest {

    private String name;

    private Double price;

    private String category;

    private Boolean available;

    private String description;

    private Integer quantity;

    private Boolean veg;

    private List<MultipartFile> images;

}