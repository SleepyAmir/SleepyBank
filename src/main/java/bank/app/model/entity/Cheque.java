package bank.app.model.entity;

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
@ToString

public class Cheque extends Account {
    private int id;
    private String number;
    private LocalDate passDate;
    private double amount;
    private String receiver;
    private String description;
}
