package ZooCapDelivery.ZooCapDelivery.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistanceResponse {

    private double distanceKm;

    private int estimatedTimeMinutes;
}