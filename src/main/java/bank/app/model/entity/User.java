package bank.app.model.entity;

import bank.app.model.entity.enums.Role;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String username;
    private String password;
    private Role role;
    private boolean active = true;
    private LocalDate registrationDate;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}