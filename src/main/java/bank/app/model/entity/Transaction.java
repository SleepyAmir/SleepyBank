package bank.app.model.entity;


import bank.app.model.entity.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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
    private LocalDateTime transactionTime;
    private String description;
}
