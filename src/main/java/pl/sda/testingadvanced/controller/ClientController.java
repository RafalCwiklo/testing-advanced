package pl.sda.testingadvanced.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sda.testingadvanced.domain.ClientService;
import pl.sda.testingadvanced.domain.model.dtos.ClientDto;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;
import pl.sda.testingadvanced.domain.model.entity.Card;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class ClientController {

    private final ClientService service;

    @GetMapping("/balance/{clientNumber}")
    ResponseEntity<Double> checkBalance(@PathVariable String clientNumber) {
        return ResponseEntity.ok(service.checkBankBalance(clientNumber));
    }

    @GetMapping("/client/{clientNumber}")
    ResponseEntity<ClientDto> getClientBasicData(@PathVariable String clientNumber) {
        return ResponseEntity.ok(service.getClientBasicData(clientNumber));
    }

    @GetMapping("/cards/{clientNumber}")
    ResponseEntity<List<Card>> getActiveCards(@PathVariable String clientNumber) {
        return ResponseEntity.ok(service.getAllActiveCards(clientNumber));
    }

    @PostMapping("/transaction")
    ResponseEntity<ClientDto> processTransaction(@RequestBody TransactionDto payload) {
        return ResponseEntity.ok(service.handleTransaction(payload));
    }

    @PostMapping("/withdrawal")
    ResponseEntity<ClientDto> processWithdrawal(@RequestBody TransactionDto payload) {
        return ResponseEntity.ok(service.handleWithdrawal(payload));
    }

}
