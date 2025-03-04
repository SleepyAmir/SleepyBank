package bank.app.model.entity;

import java.time.LocalDate;

public class Card extends Account{
    private int id;
    private String cardNumber;
    private String cvv2;
    private LocalDate expiryDate;
}
