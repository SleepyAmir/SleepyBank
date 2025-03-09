package bank.app.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Card extends Account{
    private int id;
    private String cardNumber;
    private String cvv2;
    private LocalDate expiryDate;
}
