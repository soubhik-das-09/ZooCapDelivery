package ZooCapDelivery.ZooCapDelivery.EstraService;

import ZooCapDelivery.ZooCapDelivery.Objects.DistanceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistanceService {

    @Value("${ors.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate =
            new RestTemplate();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public DistanceResponse getDistanceAndTime(
            double restaurantLat,
            double restaurantLon,
            double customerLat,
            double customerLon
    ) {

        try {

            String url =
                    "https://api.openrouteservice.org/v2/directions/driving-car";

            HttpHeaders headers =
                    new HttpHeaders();

            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = """
                    {
                      "coordinates": [
                        [%s,%s],
                        [%s,%s]
                      ]
                    }
                    """.formatted(
                    restaurantLon,
                    restaurantLat,
                    customerLon,
                    customerLat
            );

            HttpEntity<String> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

            JsonNode root =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode summary =
                    root.path("routes")
                            .get(0)
                            .path("summary");

            double distanceKm =
                    summary.get("distance")
                            .asDouble() / 1000;
            if (distanceKm <= 0) {
                distanceKm = 1.5;
            }

            int estimatedMinutes =
                    (int)(
                            summary.get("duration")
                                    .asDouble() / 60
                    );
            if (estimatedMinutes <= 0) {
                estimatedMinutes = 15;
            }

            return new DistanceResponse(
                    distanceKm,
                    estimatedMinutes
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new DistanceResponse(
                    1.5,
                    15
            );
        }
    }
}