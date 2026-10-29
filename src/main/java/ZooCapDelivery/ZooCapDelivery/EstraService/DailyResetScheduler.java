package ZooCapDelivery.ZooCapDelivery.EstraService;

import ZooCapDelivery.ZooCapDelivery.Objects.DeliveryPartner;
import ZooCapDelivery.ZooCapDelivery.Repository.DeliveryPartnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyResetScheduler {

    @Autowired
    private DeliveryPartnerRepo deliveryPartnerRepo;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTodayEarnings() {

        List<DeliveryPartner> partners =
                deliveryPartnerRepo.findAll();

        for (DeliveryPartner partner : partners) {

            partner.setTodaysEarning(0);

            deliveryPartnerRepo.save(partner);
        }
    }
}