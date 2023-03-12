package pl.sda.testingadvanced.repository;

import org.springframework.stereotype.Repository;
import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static pl.sda.testingadvanced.domain.model.entity.PaymentType.EXTERNAL;
import static pl.sda.testingadvanced.domain.model.entity.PaymentType.INCOMING;

@Repository
public class InMemoryRepository implements ClientRepository {

    private static final Map<String, Client> clients;

    static {
        clients = new HashMap<>();

        List<Transaction> transactions = getTransactions();
        List<Card> cards = getCard();

        clients.put("ABC123", Client.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .transactions(transactions)
                .cards(cards)
                .bankBalance(1_000_000.0)
                .build());
    }

    private static List<Card> getCard() {
        List<Card> cards = new ArrayList<>();
        cards.add(Card.builder()
                .cardNumber("1234-5555")
                .expirationDate(LocalDate.of(2021, AUGUST, 10))
                .build());

        cards.add(Card.builder()
                .cardNumber("9999-4321")
                .expirationDate(LocalDate.of(2026, FEBRUARY, 17))
                .build());

        cards.add(Card.builder()
                .cardNumber("0000-2121")
                .expirationDate(LocalDate.of(2025, MARCH, 30))
                .build());

        return cards;
    }

    private static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(Transaction.builder()
                .transactionDate(LocalDate.of(2021, AUGUST, 10))
                .amount(1000.0)
                .paymentType(EXTERNAL)
                .build());

        transactions.add(Transaction.builder()
                .transactionDate(LocalDate.of(2021, JANUARY, 1))
                .amount(10.0)
                .paymentType(INCOMING)
                .build());

        transactions.add(Transaction.builder()
                .transactionDate(LocalDate.of(2021, DECEMBER, 25))
                .amount(6.5)
                .paymentType(INCOMING)
                .build());

        return transactions;
    }


    @Override
    public List<Card> getAllClientsCardsByClientId(String clientId) {
        return Optional.ofNullable(clients.get(clientId))
                .map(Client::getCards)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Transaction> getAllClientTransactionsByClientId(String clientId) {
        return Optional.ofNullable(clients.get(clientId))
                .map(Client::getTransactions)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Client> getClientDataByClientId(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    @Override
    public List<Transaction> saveTransaction(Transaction transaction) {
        Client client = clients.get(transaction.getClientNumber());
        List<Transaction> transactions = client.getTransactions();
        transactions.add(transaction);
        return transactions;
    }

    @Override
    public boolean testNiceMockBoolean() {
        return true;
    }

    @Override
    public Integer testNiceMockInteger() {
        return 123;
    }

    @Override
    public Client testNiceObject() {
        return Client.builder()
                .firstName("Adam")
                .lastName("Małysz")
                .build();
    }
}
