package bank.app.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public abstract class FinancialTransaction {
    private int id;
    protected int senderAccountId;
    protected int receiverAccountId;
    protected double amount;
    protected String timestamp;
}
