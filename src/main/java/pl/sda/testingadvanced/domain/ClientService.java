package pl.sda.testingadvanced.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.testingadvanced.domain.model.dtos.ClientDto;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.Card;
import pl.sda.testingadvanced.domain.model.entity.Client;
import pl.sda.testingadvanced.domain.model.entity.EmailMessage;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;
import pl.sda.testingadvanced.domain.model.entity.Transaction;
import pl.sda.testingadvanced.exceptions.NoSuchClientException;
import pl.sda.testingadvanced.exceptions.NotEnoughResources;
import pl.sda.testingadvanced.repository.ClientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final DateTimeProvider dateTimeProvider;
    private final ClientRepository clientRepository;
    private final NotificationService notificationService;

    public Double checkBankBalance(String clientNumber) {
        Optional<Client> clientData = clientRepository.getClientDataByClientId(clientNumber);
        return clientData.map(Client::getBankBalance)
                .orElseThrow(() -> new NoSuchClientException(clientNumber));
    }

    public List<Card> getAllActiveCards(String clientNumber) {
        LocalDate currentDate = dateTimeProvider.getCurrentLocalDate();

        List<Card> allClientsCardsByClientId = clientRepository.getAllClientsCardsByClientId(clientNumber);
        return allClientsCardsByClientId.stream()
                .filter(card -> currentDate.isBefore(card.getExpirationDate()))
                .collect(Collectors.toList());
    }

    public ClientDto handleTransaction(TransactionDto transactionDto) {
        Optional<Client> client = clientRepository.getClientDataByClientId(transactionDto.getClientNumber());

        if (client.isPresent()) {
            Client clientData = client.get();
            Double bankBalance = clientData.getBankBalance();

            if (PaymentType.EXTERNAL.equals(transactionDto.getPaymentType())) {
                if (bankBalance < transactionDto.getAmount()) {
                    notificationService.sendSmsNotification();
                    throw new NotEnoughResources(transactionDto.getClientNumber());
//                    return ClientDto.toDto(clientData);
                }
                clientData.setBankBalance(bankBalance - transactionDto.getAmount());
            } else {
                clientData.setBankBalance(bankBalance + transactionDto.getAmount());
            }
            return ClientDto.toDto(clientData);
        } else {
            throw new NoSuchClientException(transactionDto.getClientNumber());
        }
    }

    public ClientDto handleWithdrawal(TransactionDto transactionDto) {
        Optional<Client> client = clientRepository.getClientDataByClientId(transactionDto.getClientNumber());
        if (client.isPresent()) {
            Client clientData = client.get();
            Double bankBalance = clientData.getBankBalance();
            if (bankBalance < transactionDto.getAmount()) {
                notificationService.sendEmailNotification(transactionDto);
                notificationService.sendLetter(new EmailMessage("Mail content"));

                return ClientDto.toDto(clientData);
            }
            Double withdrawalFee = Withdrawal.calculateWithdrawalFeeAmount(transactionDto.getAmount());

            clientData.setBankBalance(bankBalance - (transactionDto.getAmount() + withdrawalFee));
            Transaction transaction = Transaction.toEntity(transactionDto);
            transaction.setTransactionDate(dateTimeProvider.getCurrentLocalDate());

            clientRepository.saveTransaction(transaction);

            return ClientDto.toDto(clientData);
        } else {
            throw new NoSuchClientException(transactionDto.getClientNumber());
        }
    }

    public ClientDto getClientBasicData(String clientNumber) {
        Optional<Client> clientData = clientRepository.getClientDataByClientId(clientNumber);
        return clientData.map(ClientDto::toDto)
                .orElseThrow(() -> new NoSuchClientException(clientNumber));
    }
}
