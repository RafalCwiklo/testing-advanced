package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.repository.ClientRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class ClientServiceTestWithSpring {

    @Autowired
    private DateTimeProvider dateTimeProvider;
    @MockBean
//    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    ClientService clientService;

    static long start;
    static long finish;

    @BeforeAll
    static void beforeAll() {
        start = System.nanoTime();
    }

    @AfterAll
    static void afterAll() {
        finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.printf("Czas wykonania: [%s] sekund", (double) timeElapsed / 1_000_000_000);
    }

    @Test
    @Disabled
    void checkBankBalance() {
        //given

        //when
        Double bankBalance = clientService.checkBankBalance("ABC123");
        //then
        Assertions.assertEquals(1_000_000.0, bankBalance);
    }

    @Test
    void checkBankBalanceWithMockBean() {
        //given
        Mockito.when(clientRepository.getClientDataByClientId(any())).thenReturn(
                Optional.of(Client.builder()
                .bankBalance(100.0)
                .build()));
        //when
        Double bankBalance = clientService.checkBankBalance("ABC123");
        //then
        Assertions.assertEquals(100, bankBalance);
    }

}