package ZooCapDelivery.ZooCapDelivery.EstraService;

import ZooCapDelivery.ZooCapDelivery.Objects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailBody {
    @Autowired
    private EmailService emailService;
    public void sendmailToSignup(User user)
    {
        String welcomeMessage =
                "Hello " + user.getUserName() + ",\n\n" +
                        "Welcome to ZooCapDelivery! 🎉\n\n" +
                        "We're excited to have you join our community.\n\n" +
                        "Your account has been successfully created, and you can now:\n\n" +
                        "• Browse restaurants and menus\n" +
                        "• Place food orders quickly\n" +
                        "• Track your deliveries in real time\n" +
                        "• Enjoy special offers and discounts\n\n" +
                        "If you did not create this account, please contact our support team immediately.\n\n" +
                        "Thank you for choosing ZooCapDelivery.\n\n" +
                        "Happy Ordering!\n\n" +
                        "Best Regards,\n" +
                        "ZooCapDelivery Team";
        String subject = "🎉 Welcome to ZooCapDelivery - Your Account is Ready!";
        emailService.sendMail(user.getEmail(),subject,welcomeMessage );
    }
    public void sendMailToRestaurantSignup(User user) {

        String welcomeMessage =
                "Hello " + user.getUserName() + ",\n\n" +
                        "Welcome to ZooCapDelivery Partner Portal! 🎉\n\n" +
                        "Your restaurant partner account has been successfully created.\n\n" +
                        "You can now:\n\n" +
                        "• Log in to your restaurant dashboard\n" +
                        "• Add and manage menu items\n" +
                        "• Update food prices and availability\n" +
                        "• Receive and manage customer orders\n" +
                        "• Track restaurant performance\n\n" +
                        "Account Details:\n" +
                        "Username: " + user.getUserName() + "\n" +
                        "Restaurant ID: " + user.getReturantId() + "\n\n" +
                        "Please keep your login credentials secure.\n\n" +
                        "If you experience any issues, contact the ZooCapDelivery support team.\n\n" +
                        "We look forward to partnering with you and helping your business grow.\n\n" +
                        "Best Regards,\n" +
                        "ZooCapDelivery Partner Support Team";

        String subject =
                "🍽️ Welcome to ZooCapDelivery Partner Portal";

        emailService.sendMail(
                user.getEmail(),
                subject,
                welcomeMessage
        );
    }
    public void sendmailTologin(User user)
    {
        String loginMessage = """
TO %s,

We noticed a successful login to your ZooCapDelivery account.

Login Details:
• Status: Successful
• Time: %s

If this was you, no further action is required.

If you do not recognize this login activity, please change your password immediately and contact our support team.

Thank you for choosing ZooCapDelivery.

Stay safe and enjoy your experience!

Best Regards,
ZooCapDelivery Team
""".formatted(user.getUserName(), java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        String subject = "Login Successful | ZooCapDelivery";
        emailService.sendMail(user.getEmail(),subject,loginMessage );
    }
    public void sendmailToForgotPassword(User user,String otpCode)
    {
        String otpMessage = """
Hello %s,

Your password reset OTP is:

🔐 %s

This OTP will expire at: %s

Best Regards,
ZooCapDelivery Team
""".formatted(
                user.getUserName(),
                otpCode,
                LocalDateTime.now().plusMinutes(10)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
        );
        String subject = "Password Reset OTP | ZooCapDelivery";
        emailService.sendMail(user.getEmail(),subject,otpMessage );
    }
    public void sendPasswordResetSuccessMail(User user) {

        String message = """
Hello %s,

Your password has been successfully reset. ✅

Password Reset Details:
• Status: Successful
• Time: %s

You can now log in using your new password.

If you did not perform this action, please contact our support team immediately and secure your account.

Thank you for choosing ZooCapDelivery.

Stay safe!

Best Regards,
ZooCapDelivery Team
""".formatted(
                user.getUserName(),
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
        );

        String subject = "Password Reset Successful | ZooCapDelivery";

        emailService.sendMail(
                user.getEmail(),
                subject,
                message
        );
    }
    public void sendRestaurantRegistrationSuccessMail(Resturant resturant) {

        String message = """
Hello %s,

Your restaurant has been successfully registered on ZooCapDelivery! 🎉

Restaurant Details:
• Restaurant Name: %s
• Restaurant ID: %s
• Registration Time: %s

Your restaurant is now available on the ZooCapDelivery platform.

You can now:
• Add food items to your menu
• Manage food availability
• Update restaurant details
• Start receiving customer orders

If you did not perform this registration, please contact our support team immediately.

Thank you for partnering with ZooCapDelivery.

We wish you great success!

Best Regards,
ZooCapDelivery Team
""".formatted(
                resturant.getResturantName(),
                resturant.getResturantName(),
                resturant.getResturantId(),
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
        );

        String subject = "Restaurant Registration Successful | ZooCapDelivery";

        emailService.sendMail(
                resturant.getResturantEmail(),
                subject,
                message
        );
    }
    public void sendNewOrderNotificationMail(
            Order order,
            String restaurantName,
            String restaurantEmail
    ) {
        StringBuilder items = new StringBuilder();

        for(CartItem item : order.getItems()) {
            items.append("• ")
                    .append(item.getFoodName())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append("\n");
        }
        String message = """
Hello %s,

You have received a new order on ZooCapDelivery! 🎉

Order Details:
• Order ID: %s
• Customer: %s
• Restaurant ID: %s
• Total Amount: ₹%.2f
• Order Status: %s
• Order Time: %s
.Ordered Items: %s

Please review and accept the order as soon as possible.

You can now:
• View ordered items
• Accept or reject the order
• Update order status
• Start preparing the food

Thank you for partnering with ZooCapDelivery.

Best Regards,
ZooCapDelivery Team
""".formatted(
                restaurantName,
                order.getOrderId(),
                order.getUserName(),
                order.getRestaurantId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderTime(),
                items
        );

        String subject =
                "🍽️ New Order Received | Order #" +
                        order.getOrderId();

        emailService.sendMail(
                restaurantEmail,
                subject,
                message
        );
    }
    public void sendOrderCancellationMail(
            Order order,
            String userEmail
    ) {

        StringBuilder items = new StringBuilder();

        for (CartItem item : order.getItems()) {
            items.append("• ")
                    .append(item.getFoodName())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append("\n");
        }

        String message = """
Hello %s,

We regret to inform you that your order has been cancelled by the restaurant.

Order Details:
• Order ID: %s
• Restaurant ID: %s
• Total Amount: ₹%.2f
• Order Status: CANCELLED
• Order Time: %s

Ordered Items:
%s

Reason:
The restaurant is currently unable to fulfill this order due to operational constraints, item unavailability, or other unforeseen circumstances.

If any payment was made online, the refund process will be initiated according to our refund policy.

We sincerely apologize for the inconvenience caused.

You can place a new order from another restaurant available on ZooCapDelivery.

Thank you for your understanding.

Best Regards,
ZooCapDelivery Team
""".formatted(
                order.getUserName(),
                order.getOrderId(),
                order.getRestaurantId(),
                order.getTotalAmount(),
                order.getOrderTime(),
                items.toString()
        );

        String subject =
                "❌ Order Cancelled | Order #" +
                        order.getOrderId();

        emailService.sendMail(
                userEmail,
                subject,
                message
        );
    }
    public void sendMailToDeliveryPartner(DeliveryPartner deliveryPartner) {

        String message =
                "Hello " + deliveryPartner.getName() + ",\n\n" +

                        "🎉 Congratulations! Welcome to ZooCapDelivery Delivery Partner Program.\n\n" +

                        "Your delivery partner account has been successfully registered.\n\n" +

                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Delivery Partner Details\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +

                        "Partner ID      : " + deliveryPartner.getDeliveryPartnerId() + "\n" +
                        "Username        : " + deliveryPartner.getUsername() + "\n" +
                        "Name            : " + deliveryPartner.getName() + "\n" +
                        "Email           : " + deliveryPartner.getEmail() + "\n" +
                        "Phone           : " + deliveryPartner.getPhone() + "\n" +
                        "Vehicle Type    : " + deliveryPartner.getVehicleType() + "\n" +
                        "Vehicle Number  : " + deliveryPartner.getVehicleNumber() + "\n" +
                        "License Number  : " + deliveryPartner.getLicenseNumber() + "\n\n" +

                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Current Status\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +

                        "Online          : OFFLINE\n" +
                        "Available       : YES\n" +
                        "Rating          : " + deliveryPartner.getRating() + "\n" +
                        "Total Deliveries: " + deliveryPartner.getTotalDeliveries() + "\n\n" +

                        "You can now log in to your Delivery Partner Dashboard and:\n\n" +

                        "✔ Receive delivery requests\n" +
                        "✔ View assigned orders\n" +
                        "✔ Update delivery status\n" +
                        "✔ Track your deliveries\n" +
                        "✔ View your delivery history and earnings\n\n" +

                        "Please keep your vehicle documents and license up to date.\n" +
                        "Always ensure timely and safe deliveries while maintaining excellent customer service.\n\n" +

                        "If you did not request this registration, please contact ZooCapDelivery Support immediately.\n\n" +

                        "Thank you for becoming a valued member of the ZooCapDelivery family.\n\n" +

                        "Ride Safe! 🚴\n\n" +

                        "Best Regards,\n" +
                        "ZooCapDelivery Team";

        String subject =
                "🚴 Welcome to ZooCapDelivery - Delivery Partner Registration Successful";

        emailService.sendMail(
                deliveryPartner.getEmail(),
                subject,
                message
        );
    }
}
