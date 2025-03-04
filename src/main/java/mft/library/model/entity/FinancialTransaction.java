package mft.library.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public abstract class FinancialTransaction extends BankEntity{
    protected int senderAccountId;
    protected int receiverAccountId;
    protected double amount;
    protected String timestamp;
}
