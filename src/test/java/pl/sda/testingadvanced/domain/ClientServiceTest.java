package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.testingadvanced.domain.model.dtos.ClientDto;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;
import pl.sda.testingadvanced.repository.ClientRepository;

import java.util.List;
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
    @Mock
    NotificationService notificationServiceMock;
    NotificationService notificationServiceStub;


    @BeforeEach
    void setUp() {

        clientServiceWithMock = new ClientService(dateTimeProvider, clientRepository, notificationServiceMock);
        clientServiceWithStub = new ClientService(dateTimeProviderWithStub, clientRepositoryWithStub, notificationServiceStub);
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
    void getAllActiveCardsWithStub() {
        //given
        //when
        List<Card> activeCards = clientServiceWithStub.getAllActiveCards("CCC123");
        //then
        Assertions.assertEquals(2, activeCards.size());
    }

    @Test
    void handleTransaction() {
        //given
        Mockito.when(clientRepository.getClientDataByClientId("123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(200.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);

        //when
        ClientDto clientDto = clientServiceWithMock.handleTransaction(transactionDto);
        //then
        Mockito.verify(notificationServiceMock, Mockito.times(1)).sendSmsNotification();
        Mockito.verifyNoMoreInteractions(notificationServiceMock);

    }

    @Test
    void handleWithdrawal() {
    }

    @Test
    void getClientBasicData() {
    }
}