package pl.sda.testingadvanced.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Data;
import pl.sda.testingadvanced.domain.model.dtos.TransactionDto;

import java.time.LocalDate;

@Data
@Builder
public class Transaction {
    private String clientNumber;
    private PaymentType paymentType;
    private Double amount;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate transactionDate;

    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .clientNumber(dto.getClientNumber())
                .amount(dto.getAmount())
                .paymentType(dto.getPaymentType())
                .build();
    }
}
