package ZooCapDelivery.ZooCapDelivery.Configuration;

import ZooCapDelivery.ZooCapDelivery.Objects.Address;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new StringToAddressConverter());
        return new MongoCustomConversions(converters);
    }

    @ReadingConverter
    public static class StringToAddressConverter implements Converter<String, Address> {
        @Override
        public Address convert(@org.springframework.lang.NonNull String source) {
            Address address = new Address();
            address.setStreet(source);
            address.setCity("");
            address.setState("");
            address.setPincode("");
            address.setLandmark("");
            address.setHouseNo("");
            address.setPhoneNumber("");
            return address;
        }
    }
}
