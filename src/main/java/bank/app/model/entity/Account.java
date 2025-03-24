package bank.app.model.entity;

import bank.app.model.entity.enums.AccountType;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public abstract class Account {
    private int id;
    private User user;
    private AccountType accountType;
    private double balance;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}