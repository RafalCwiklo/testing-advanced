package pl.sda.testingadvanced.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendSmsNotification() {
        //odwołanie do systemu zewnetrznego do wysylki sms
        log.info("Brak wystarczajacych srodkow na koncie");
    }
}
