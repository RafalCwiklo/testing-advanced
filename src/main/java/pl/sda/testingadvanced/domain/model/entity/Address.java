package pl.sda.testingadvanced.domain.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String city;
    private String street;
    private String zipCode;

}
