package bank.app.model.service;

import bank.app.model.entity.User;
import bank.app.model.repository.UserRepository;
import java.util.List;

public class UserService implements Service<User, Integer> {

    public User authenticate(String username, String password) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password) && u.isActive())
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public void save(User user) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            userRepository.save(user);
        }
    }

    @Override
    public void edit(User user) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            userRepository.edit(user);
        }
    }

    @Override
    public void remove(Integer id) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            userRepository.remove(id);
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            return userRepository.findAll();
        }
    }

    @Override
    public User findById(Integer id) throws Exception {
        try (UserRepository userRepository = new UserRepository()) {
            return userRepository.findById(id);
        }
    }
}