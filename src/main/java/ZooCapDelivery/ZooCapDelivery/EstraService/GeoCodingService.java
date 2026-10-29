package ZooCapDelivery.ZooCapDelivery.EstraService;

import ZooCapDelivery.ZooCapDelivery.Objects.Address;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class GeoCodingService {

    @Value("${ors.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate =
            new RestTemplate();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public Address populateCoordinates(Address address) {

        try {
            // Check if address already contains valid coordinates selected via location API / Map
            if (address != null && address.getLatitude() != null && address.getLongitude() != null
                    && address.getLatitude() >= 6.0 && address.getLatitude() <= 38.0
                    && address.getLongitude() >= 68.0 && address.getLongitude() <= 98.0) {
                log.info("Address already has valid coordinates: {}, {}", address.getLatitude(), address.getLongitude());
                return address;
            }

            String fullAddress =
                    address.getStreet() + ", " +
                            address.getCity() + ", " +
                            address.getState() + ", India";

            Address result =
                    fetchCoordinates(fullAddress, address);

            if(result.getLatitude() != null &&
                    result.getLongitude() != null) {

                return result;
            }

            log.info(
                    "Trying fallback geocoding using city/state"
            );

            String fallbackAddress =
                    address.getCity() + ", " +
                            address.getState() + ", India";

            Address finalResult = fetchCoordinates(
                    fallbackAddress,
                    address
            );

            if (finalResult.getLatitude() == null || finalResult.getLongitude() == null) {
                log.warn("Geocoding returned null coordinates. Applying default coordinates: 22.5726, 88.3639");
                finalResult.setLatitude(22.5726);
                finalResult.setLongitude(88.3639);
            }

            return finalResult;

        } catch (Exception e) {

            log.error(
                    "Geocoding Error : {}",
                    e.getMessage()
            );

            if (address.getLatitude() == null || address.getLongitude() == null) {
                address.setLatitude(22.5726);
                address.setLongitude(88.3639);
            }

            return address;
        }
    }

    private Address fetchCoordinates(
            String query,
            Address address) {

        try {

            String encodedAddress =
                    URLEncoder.encode(
                            query,
                            StandardCharsets.UTF_8
                    );

            String url =
                    "https://api.openrouteservice.org/geocode/search?text="
                            + encodedAddress;

            System.out.println(url);

            HttpHeaders headers =
                    new HttpHeaders();

            headers.set(
                    "Authorization",
                    apiKey
            );

            HttpEntity<String> entity =
                    new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            String.class
                    );

            System.out.println(response.getBody());

            JsonNode root =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode features =
                    root.path("features");

            if(features.isEmpty()) {

                log.error(
                        "Coordinates not found for {}",
                        query
                );

                return address;
            }

            for(JsonNode feature : features) {

                JsonNode coordinates =
                        feature.path("geometry")
                                .path("coordinates");

                if(coordinates.size() < 2) {
                    continue;
                }

                double longitude =
                        coordinates.get(0)
                                .asDouble();

                double latitude =
                        coordinates.get(1)
                                .asDouble();

                /*
                 * India boundary check
                 */
                if(latitude >= 6 &&
                        latitude <= 38 &&
                        longitude >= 68 &&
                        longitude <= 98) {

                    address.setLatitude(
                            latitude
                    );

                    address.setLongitude(
                            longitude
                    );

                    log.info(
                            "Coordinates Found : {}, {}",
                            latitude,
                            longitude
                    );

                    return address;
                }
            }

            log.error(
                    "No valid Indian coordinates found for {}",
                    query
            );

            return address;

        } catch (Exception e) {

            log.error(
                    "Error while fetching coordinates : {}",
                    e.getMessage()
            );

            return address;
        }
    }
}