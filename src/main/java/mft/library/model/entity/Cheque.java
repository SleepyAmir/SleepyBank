package mft.library.model.entity;

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

public class Cheque {

    private int chequeId;
    private int issuerAccountId;//FK
    private int recipientAccountId;//FK
    private double amount;
    private String chequeAddress;
    private String status;
    private String issueDate;

}
