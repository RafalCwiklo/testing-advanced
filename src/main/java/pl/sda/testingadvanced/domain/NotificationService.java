package pl.sda.testingadvanced.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sda.testingadvanced.domain.model.entity.EmailMessage;

@Service
@Slf4j
public class NotificationService {

    public void sendSmsNotification() {
        //odwołanie do systemu zewnetrznego do wysylki sms
        log.info("Brak wystarczajacych srodkow na koncie");
    }

    public void sendLetter() {
        log.info("Brak wystarczajacych srodkow na koncie");
    }

    public void sendEmailNotification(EmailMessage message) {
        log.info("Email sent: {}", message.getMessage());

    }
}
