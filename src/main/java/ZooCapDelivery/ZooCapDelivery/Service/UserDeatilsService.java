package ZooCapDelivery.ZooCapDelivery.Service;

import ZooCapDelivery.ZooCapDelivery.Objects.User;
import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import ZooCapDelivery.ZooCapDelivery.Objects.Resturant;
import ZooCapDelivery.ZooCapDelivery.Repository.UserRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.DeliveryPartnerRepo;
import ZooCapDelivery.ZooCapDelivery.Repository.ResturantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDeatilsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DeliveryPartnerRepo deliveryPartnerRepo;

    @Autowired
    private ResturantRepo resturantRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByuserName(username);
        if (user == null) {
            user = userRepo.findByEmail(username);
        }
        if (user == null) {
            user = userRepo.findByPhNumber(username);
        }
        if (user != null) {
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
                    .build();
        }

        DeliveryPartner partner = deliveryPartnerRepo.findDeliveryPartnerByUsername(username);
        if (partner != null) {
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(partner.getUsername())
                    .password(partner.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_DELIVERY"))
                    .build();
        }

        // Restaurant owners: JWT subject is the resturantId
        Resturant restaurant = resturantRepo.findByResturantId(username);
        if (restaurant != null && restaurant.getPassword() != null) {
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(restaurant.getResturantId())
                    .password(restaurant.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_RESTAURANT"))
                    .build();
        }

        throw new UsernameNotFoundException(username);
    }
}
