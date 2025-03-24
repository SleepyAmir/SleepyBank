package bank.app.controller;

import bank.app.model.entity.User;
import bank.app.model.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;

public class UserInfoController {
    @Setter
    private UserService userService;
    @Setter
    private int userId;
    private boolean isEditMode = false;

    @FXML private TextField firstNameInfoTxt;
    @FXML private TextField lastNameInfoTxt;
    @FXML private TextField emailAddressInfoTxt;
    @FXML private TextField dateOfBirthInfoTxt;
    @FXML private TextField phoneNumberInfoTxt;
    @FXML private TextField addressInfoTxt;
    @FXML private TextField userNameInfoTxt;
    @FXML private TextField passwordInfoTxt;
    @FXML private Button editBtn;

    @FXML
    private void initialize() {
        editBtn.setOnAction(event -> toggleEditMode());
    }

    public void loadData() throws Exception {
        if (userService == null) {
            System.out.println("Error: UserService not initialized");
            return;
        }
        populateUserInfo();
    }

    private void saveUserInfo() {
        try {
            User user = userService.findById(userId);
            String email = emailAddressInfoTxt.getText();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("Invalid email format");
                return;
            }
            user.setEmail(email);
            user.setPhone(phoneNumberInfoTxt.getText());
            user.setAddress(addressInfoTxt.getText());
            user.setUsername(userNameInfoTxt.getText());
            user.setPassword(passwordInfoTxt.getText());
            userService.edit(user);
        } catch (Exception e) {
            System.out.println("Error saving user info: " + e.getMessage());
        }
    }

    private void populateUserInfo() throws Exception {
        User user = userService.findById(userId);
        firstNameInfoTxt.setText(user.getFirstName());
        lastNameInfoTxt.setText(user.getLastName());
        emailAddressInfoTxt.setText(user.getEmail());
        dateOfBirthInfoTxt.setText(user.getBirthDate().toString());
        phoneNumberInfoTxt.setText(user.getPhone());
        addressInfoTxt.setText(user.getAddress());
        userNameInfoTxt.setText(user.getUsername());
        passwordInfoTxt.setText(user.getPassword());
    }

    private void toggleEditMode() {
        if (!isEditMode) {
            isEditMode = true;
            setEditable(true);
            editBtn.setText("Save");
        } else {
            isEditMode = false;
            setEditable(false);
            editBtn.setText("Edit");
            saveUserInfo();
        }
    }

    private void setEditable(boolean editable) {
        emailAddressInfoTxt.setEditable(editable);
        phoneNumberInfoTxt.setEditable(editable);
        addressInfoTxt.setEditable(editable);
        userNameInfoTxt.setEditable(editable);
        passwordInfoTxt.setEditable(editable);
    }
}