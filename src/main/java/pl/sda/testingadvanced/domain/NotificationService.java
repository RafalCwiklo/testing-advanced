package pl.sda.testingadvanced.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.EmailMessage;

@Service
@Slf4j
public class NotificationService {

    public void sendSmsNotification() {
        log.info("Brak wystarczających środków na koncie! [wysłano sms]");
    }

    public void sendEmailNotification(TransactionDto dto) {
        log.info("Brak wystarczających środków na koncie! Próba wykonania transkacji {}", dto);
    }

    public void sendLetter(EmailMessage emailMessage) {
        log.info("Message sent: {}", emailMessage.getMessage());
    }
}
