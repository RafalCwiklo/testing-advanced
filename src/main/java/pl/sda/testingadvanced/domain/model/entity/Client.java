package pl.sda.testingadvanced.domain.model.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class Client {

    private String firstName;
    private String lastName;
    private Double bankBalance;
    private List<Transaction> transactions;
    private List<Card> cards;
}
