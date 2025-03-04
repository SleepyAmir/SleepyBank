package mft.library.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Account {

    private int userId; // Foreign Key
    private int accountId;
    private String accountType;
    private double balance;
    private String cardNumber;
    private String cvv2;
    private String expiryDate;
}
