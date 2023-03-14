package pl.sda.testingadvanced.repository;

import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    List<Card> getAllClientsCardsByClientId(String clientId);
    List<Transaction> getAllClientTransactionsByClientId(String clientId);
    Optional<Client> getClientDataByClientId(String clientId);
    List<Transaction> saveTransaction(Transaction transaction);
}
