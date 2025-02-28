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
@ToString

public class Account {
    private String username;
    private String password;
    private int cardNumber;
    private int cvv2Code;
    private LocalDate expiryDate;
    private double balance;
}
