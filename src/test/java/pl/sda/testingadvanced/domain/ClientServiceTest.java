package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.repository.ClientRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    ClientService clientServiceWithMock;
    ClientService clientServiceWithStub;
    DateTimeProvider dateTimeProviderWithStub = new DateTimeProvider();
    @Mock
    DateTimeProvider dateTimeProvider;
    @Mock
    ClientRepository clientRepository;
    ClientRepository clientRepositoryWithStub = new RepositoryStub();


    @BeforeEach
    void setUp() {

        clientServiceWithMock = new ClientService(dateTimeProvider, clientRepository);
        clientServiceWithStub = new ClientService(dateTimeProviderWithStub, clientRepositoryWithStub);
    }

    @Test
    void checkBankBalanceWithMock() {
        //given
        Mockito.when(clientRepository.getClientDataByClientId("ABC123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        Mockito.when(clientRepository.getClientDataByClientId("ABC999"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(200.0)
                        .build()));
        //when
        Double bankBalance = clientServiceWithMock.checkBankBalance("ABC123");
        Double bankBalance2 = clientServiceWithMock.checkBankBalance("ABC999");
        //then
        Assertions.assertEquals(100.0, bankBalance);
        Assertions.assertEquals(200.0, bankBalance2);
    }

    @Test
    void checkBankBalanceWithStub() {
        //given
        //when
        Double bankBalance = clientServiceWithStub.checkBankBalance("CCC123");
        //then
        Assertions.assertEquals(100.0, bankBalance);
    }

    @Test
    void getAllActiveCards() {
    }

    @Test
    void handleTransaction() {
    }

    @Test
    void handleWithdrawal() {
    }

    @Test
    void getClientBasicData() {
    }
}