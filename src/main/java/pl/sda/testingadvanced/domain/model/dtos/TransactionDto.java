package pl.sda.testingadvanced.domain.model.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.sda.testingadvanced.domain.model.entity.PaymentType;

@Data
@NoArgsConstructor
public class TransactionDto {
    private String clientNumber;
    private Double amount;
    private PaymentType paymentType;
}
