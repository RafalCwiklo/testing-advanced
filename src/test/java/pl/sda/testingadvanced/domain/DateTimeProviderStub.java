package pl.sda.testingadvanced.domain;

import java.time.LocalDate;

public class DateTimeProviderStub extends DateTimeProvider{
    @Override
    LocalDate getCurrentLocalDate() {
        return LocalDate.now().plusDays(7);
    }
}
