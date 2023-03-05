package pl.sda.testingadvanced.domain.model.dtos;

import lombok.Builder;
import lombok.Data;
import pl.sda.testingadvanced.domain.model.entity.Client;

@Data
@Builder
public class ClientDto {
    private String firstName;
    private String lastName;
    private Double currentBankBalance;

    public static ClientDto toDto(Client clientData) {
        return ClientDto.builder()
                .firstName(clientData.getFirstName())
                .lastName(clientData.getLastName())
                .currentBankBalance(clientData.getBankBalance())
                .build();
    }
}
