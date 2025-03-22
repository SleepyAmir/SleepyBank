package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class UserManager {
    private final UserService userService;

    public UserManager(UserService userService) {
        this.userService = userService;
    }

    public void saveUser(User user) throws Exception {
        userService.save(user);
    }

    public void editUser(User user) throws Exception {
        userService.edit(user);
    }

    public void removeUser(int userId) throws Exception {
        userService.remove(userId);
    }

    public List<User> findAllUsers() throws Exception {
        return userService.findAll();
    }

    public List<User> filterUsers(String firstName, String lastName, String phone, String email,
                                  LocalDate birthDate, String address, String username, String password) throws Exception {
        return userService.findAll().stream()
                .filter(u -> matchesField(u.getFirstName(), firstName))
                .filter(u -> matchesField(u.getLastName(), lastName))
                .filter(u -> matchesField(u.getPhone(), phone))
                .filter(u -> matchesField(u.getEmail(), email))
                .filter(u -> matchesDate(u.getBirthDate(), birthDate))
                .filter(u -> matchesField(u.getAddress(), address))
                .filter(u -> matchesField(u.getUsername(), username))
                .filter(u -> matchesField(u.getPassword(), password))
                .collect(Collectors.toList());
    }

    private boolean matchesField(String value, String filter) {
        return filter == null || filter.isEmpty() || (value != null && value.toLowerCase().contains(filter.toLowerCase()));
    }

    private boolean matchesDate(LocalDate value, LocalDate filter) {
        return filter == null || (value != null && value.equals(filter));
    }
}