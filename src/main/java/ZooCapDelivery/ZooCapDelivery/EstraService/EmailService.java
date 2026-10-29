package ZooCapDelivery.ZooCapDelivery.EstraService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
   public void sendMail(String to, String subject, String content){
      try{
          SimpleMailMessage message = new SimpleMailMessage();
          message.setFrom("dassoubhik1101@gmail.com");
          message.setTo(to);
          message.setSubject(subject);
          message.setText(content);
          mailSender.send(message);
          System.out.println("Mail Sent to "+to);
      }catch(Exception e){
          log.error(e.getMessage());
          System.out.println(e.getMessage());
      }
   }
}

