package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.testingadvanced.domain.model.dtos.ClientDto;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.EmailMessage;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;
import pl.sda.testingadvanced.exceptions.NoSuchClientException;
import pl.sda.testingadvanced.exceptions.NotEnoughResourcesException;
import pl.sda.testingadvanced.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mockStatic;

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
    NotificationService notificationServiceStub = new NotificationService();


    @BeforeEach
    void setUp() {

        clientServiceWithMock = new ClientService(dateTimeProvider, clientRepository, notificationServiceMock);
        clientServiceWithStub = new ClientService(dateTimeProviderWithStub, clientRepositoryWithStub, notificationServiceStub);
    }

    @Test
    void checkBankBalanceWithMock(Double double1, Double double2) {
        //given
        Mockito.when(clientRepository.getClientDataByClientId("ABC123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(double1)
                        .build()));

//        Mockito.when(clientRepository.getClientDataByClientId("ABC999"))
//                .thenReturn(Optional.of(Client.builder()
//                        .bankBalance(200.0)
//                        .build()));
        //when
        Double bankBalance = clientServiceWithMock.checkBankBalance("ABC123");
        Double bankBalance2 = clientServiceWithMock.checkBankBalance("ABC999");
        //then
        Assertions.assertEquals(double2, bankBalance);
        Assertions.assertEquals(200.0, bankBalance2);
    }

    @ParameterizedTest
    @ArgumentsSource(MyArgumentProvider.class)
    void parametrizedCheckBankBalanceWithMock(Double double1, Double double2) {
        //given
        Mockito.when(clientRepository.getClientDataByClientId(Mockito.anyString()))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(double1)
                        .build()));
        //when
        Double bankBalance = clientServiceWithMock.checkBankBalance("ABC999");
        //then
        Assertions.assertEquals(double2, bankBalance);
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
        transactionDto.setAmount(50.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);

        //when
        ClientDto clientDto = clientServiceWithMock.handleTransaction(transactionDto);
        //then
        Mockito.verify(notificationServiceMock, Mockito.times(0)).sendSmsNotification();
        Mockito.verifyNoMoreInteractions(notificationServiceMock);

    }

    @Test
    void shouldSendEmailWithProperContent() {
        //given
        ArgumentCaptor<EmailMessage> captorDto = ArgumentCaptor.forClass(EmailMessage.class);

        Mockito.when(clientRepository.getClientDataByClientId("123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(200.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);
        //when
        clientServiceWithMock.handleWithdrawal(transactionDto);

        //then
        Mockito.verify(notificationServiceMock).sendEmailNotification(captorDto.capture());
        Assertions.assertEquals("mail content",captorDto.getValue().getMessage());
    }

    @Test
    void handleWithdrawal() {
        //given
        Mockito.when(clientRepository.getClientDataByClientId(Mockito.anyString()))
                .thenReturn(Optional.of(Client.builder()
                                .bankBalance(100.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(200.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);

        //when
        Assertions.assertThrows(NotEnoughResourcesException.class,
                () -> clientServiceWithMock.handleWithdrawal(transactionDto));
        //then
    }

    @Test
    void getClientBasicData() {
        //given
        //when //then
        Assertions.assertThrows(NoSuchClientException.class,
                () -> clientServiceWithStub.getClientBasicData("ABC123"));
//                "Nie znaleziono klienta o takim identyfikatorze: [1]");
    }

    @ParameterizedTest
    @MethodSource("getFakeArgumentsForTest")
    void checkWithMethodSource(int a, int b, int oczekiwanaSuma) {
        //given
        //when
        int faktycznyWynik = a + b;
        //then
        Assertions.assertEquals(oczekiwanaSuma, faktycznyWynik);
    }

    private static Stream<Arguments> getFakeArgumentsForTest() {
        return Stream.of(Arguments.of(1, 2, 3),
                Arguments.of(10, 2, 12),
                Arguments.of(0, 2, 1),
                Arguments.of(7, 5, 12)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"SDA", "test", "123"})
    void checkIfStringIsNotNull(String input) {
        //given
        //when and then
        Assertions.assertNotNull(input);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void checkIfExceptionIsThrownWhenClientDoesntExist(String input) {
        //given
        //when and then
        Assertions.assertThrows(NoSuchClientException.class,
                () -> clientServiceWithStub.getClientBasicData(input));
    }

    @Test
    void checkMockedWithdrawalFee() {
        //given
        try (MockedStatic mockStatic = mockStatic(Withdrawal.class)) {

            mockStatic.when(() -> Withdrawal.getCurrentPercentageFeeAmount()).thenReturn(5.0);
            //when
            Double feePercentage = Withdrawal.getCurrentPercentageFeeAmount();
            //then
            Assertions.assertEquals(5.0, feePercentage);
        }

        //when
        Double feePercentageOutside = Withdrawal.getCurrentPercentageFeeAmount();
        //then
        Assertions.assertEquals(1.5, feePercentageOutside);

    }
}