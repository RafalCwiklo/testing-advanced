package pl.sda.testingadvanced.domain;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.testingadvanced.domain.model.dtos.ClientDto;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.EmailMessage;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;
import pl.sda.testingadvanced.exceptions.NoSuchClientException;
import pl.sda.testingadvanced.exceptions.NotEnoughResources;
import pl.sda.testingadvanced.repository.ClientRepository;
import pl.sda.testingadvanced.repository.InMemoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    static long start;
    static long finish;
    DateTimeProvider dateTimeProviderStub = new DateTimeProvider();
    @Mock
    DateTimeProvider dateTimeProviderMock;
    @Mock
    ClientRepository clientRepositoryMock;
    @Mock
    NotificationService notificationServiceMock;
    ClientService clientService;
    ClientService clientServiceWithStub;
    ClientRepository clientRepositoryStub = new InMemoryRepository();

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

    private static Stream<Arguments> getFakeTransactions() {
        return Stream.of(
                Arguments.of(200_000.0, PaymentType.EXTERNAL, 800_000.0),
                Arguments.of(1_000_000.0, PaymentType.INCOMING, 2_000_000.0),
                Arguments.of(1_000_000.0, PaymentType.EXTERNAL, 0.0)
        );
    }

    private static Stream<Arguments> getFakeExceptions() {
        return Stream.of(
                Arguments.of("111", 1.0, new NoSuchClientException("111"), "Nie znaleziono klienta o takim identyfikatorze: [111]"),
                Arguments.of("ABC123", 2_000_000.0, new NotEnoughResources("ABC123"), "Brak wystarczających środków u klienta: [ABC123]")
        );
    }

    @BeforeEach
    void setUp() {
        clientService = new ClientService(dateTimeProviderMock, clientRepositoryMock, notificationServiceMock);
        clientServiceWithStub = new ClientService(dateTimeProviderStub, clientRepositoryStub, notificationServiceMock);
    }

    @Test
    void checkBankBalanceWithMock() {
        //given
        when(clientRepositoryMock.getClientDataByClientId("999"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        when(clientRepositoryMock.getClientDataByClientId("111"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(111.0)
                        .build()));
        //when
        Double bankBalance = clientService.checkBankBalance("999");
        Double bankBalance111 = clientService.checkBankBalance("111");

        //then
        Assertions.assertEquals(100.0, bankBalance);
        Assertions.assertEquals(111.0, bankBalance111);
    }

    @Test
    void checkBankBalanceWithStub() {
        //when
        Double bankBalance = clientServiceWithStub.checkBankBalance("ABC123");
        //then
        Assertions.assertEquals(1_000_000.0, bankBalance);
    }

    @Test
    void getAllActiveCards() {
        //given
        //when
        List<Card> activeCards = clientServiceWithStub.getAllActiveCards("ABC123");
        //then
        Assertions.assertEquals(2, activeCards.size());
    }

    @Test
    @Disabled
    void handleTransaction() {
        //given
        when(clientRepositoryMock.getClientDataByClientId("123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        when(clientRepositoryMock.getClientDataByClientId("ABC"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(300.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(200.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.EXTERNAL);

        TransactionDto transactionDto2 = new TransactionDto();
        transactionDto2.setAmount(100.0);
        transactionDto2.setClientNumber("ABC");
        transactionDto2.setPaymentType(PaymentType.EXTERNAL);

        //when
        ClientDto clientDto = clientService.handleTransaction(transactionDto);
        ClientDto clientDto2 = clientService.handleTransaction(transactionDto2);

        //then
        verify(notificationServiceMock, times(1)).sendSmsNotification();
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Test
    void shouldSendEmailWithProperContent() {
        //given

        ArgumentCaptor<TransactionDto> captorDto = ArgumentCaptor.forClass(TransactionDto.class);

        when(clientRepositoryMock.getClientDataByClientId("123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(100.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(200.0);
        transactionDto.setClientNumber("123");
        transactionDto.setPaymentType(PaymentType.WITHDRAWAL);

        //when

        clientService.handleWithdrawal(transactionDto);

        //then
        verify(notificationServiceMock).sendEmailNotification(captorDto.capture());

        Assertions.assertEquals(transactionDto, captorDto.getValue());

    }

    @Test
    void shouldThrowCustomExceptionWhenClientDoesntExist() {
        //given
        //when//then
        Assertions.assertThrows(NoSuchClientException.class, () -> clientServiceWithStub.getClientBasicData("ABC"),
                "Nie znaleziono klienta o takim identyfikatorze: [ABC]");


    }

    @ParameterizedTest
    @ValueSource(strings = {"test1", "test2", "null"})
    void shouldReturnProperBankBalance(String testWord) {
        Assertions.assertNotNull(testWord);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenNullIsPased(String input) {
        Assertions.assertThrows(NoSuchClientException.class, () -> clientServiceWithStub.getClientBasicData(input));
    }

    @DisplayName("Test parametryzowany z użyciem adnotacji ArgumentSource")
    @ParameterizedTest()
    @ArgumentsSource(MyArgumentProvider.class)
    void testWithArgumentsSource(int a, int b, int sum) {
        Assertions.assertEquals(sum, a + b);
    }

    @DisplayName("Zadanie 5")
    @ParameterizedTest()
    @MethodSource("getFakeTransactions")
    void testHandleTransactionWithMethodSource(Double amount, PaymentType paymentType, Double expectedBankBalance) {
        //given
        when(clientRepositoryMock.getClientDataByClientId("ABC123"))
                .thenReturn(Optional.of(Client.builder()
                        .bankBalance(1_000_000.0)
                        .build()));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setClientNumber("ABC123");
        transactionDto.setAmount(amount);
        transactionDto.setPaymentType(paymentType);
        //when
        ClientDto clientDto = clientService.handleTransaction(transactionDto);
        //then
        Assertions.assertEquals(expectedBankBalance, clientDto.getCurrentBankBalance());
    }

    @DisplayName("Zadanie 5")
    @ParameterizedTest()
    @MethodSource("getFakeExceptions")
    void testThrowingExceptionWithMethodSource(String clientNumber, Double amount, Exception e, String exceptionMessage) {
        //given
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setClientNumber(clientNumber);
        transactionDto.setAmount(amount);
        transactionDto.setPaymentType(PaymentType.EXTERNAL);
        //when
        Assertions.assertThrows(e.getClass(), () -> clientServiceWithStub.handleTransaction(transactionDto), exceptionMessage);

    }

    @DisplayName("Mock static method")
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

    @DisplayName("Mock constructor")
    @Test
    void checkNewObjectCreation() {
        //given
        try (MockedConstruction<EmailMessage> mocked = mockConstruction(EmailMessage.class)) {

            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(2000000.0);
            transactionDto.setClientNumber("ABC123");
            transactionDto.setPaymentType(PaymentType.WITHDRAWAL);

            //when
            clientServiceWithStub.handleWithdrawal(transactionDto);

            //then
            Assertions.assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void handleWithdrawal() {
        //todo
    }

    @Test
    void getClientBasicData() {
        //todo
    }


}