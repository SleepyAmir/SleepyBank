package bank.app.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class Account{
    private int id;
    private int userId; // Foreign Key
    private String accountType;
    private double balance;
    //    private int accountId;
//    private String cardNumber;
//    private String cvv2;
//    private String expiryDate;
}
