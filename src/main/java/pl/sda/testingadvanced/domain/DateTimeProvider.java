package pl.sda.testingadvanced.domain;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateTimeProvider {
    LocalDate getCurrentLocalDate() {
        //communicate to external service to fetch current date/time in specific time zone
        return LocalDate.now();
    }
}
