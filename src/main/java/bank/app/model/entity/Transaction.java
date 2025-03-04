package bank.app.model.entity;


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

public class Transaction extends FinancialTransaction{
    private int transactionId;
    private int senderAccountId;//FK
    private int receiverAccountId;//FK
    private double amount;
    private String transactionType;
    private String timestamp;

}
