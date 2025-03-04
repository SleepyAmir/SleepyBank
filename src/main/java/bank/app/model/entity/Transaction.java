package bank.app.model.entity;


import bank.app.model.entity.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString

public class Transaction {
    private int id;
    private Account sourceAccount;
    private Account destinationAccount;
    private double amount;
    private TransactionType transactionType;
    private String timestamp;

}
