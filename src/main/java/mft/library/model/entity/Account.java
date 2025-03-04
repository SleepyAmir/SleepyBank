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

public class Account extends BankEntity{

    private int userId; // Foreign Key
    private String accountType;
    private double balance;
    //    private int accountId;
//    private String cardNumber;
//    private String cvv2;
//    private String expiryDate;
}
