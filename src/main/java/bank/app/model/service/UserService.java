package bank.app.model.service;

import bank.app.model.entity.User;
import bank.app.model.repository.UserRepository;

import java.util.Collections;
import java.util.List;

public class UserService implements Service <User, Integer> {


    @Override
    public void save(User user) throws Exception {
        try(UserRepository userRepository = new UserRepository()){
            userRepository.save(user);
        }
    }

    @Override
    public void edit(User user) throws Exception {

    }

    @Override
    public void remove(Integer id) throws Exception {

    }

    @Override
    public List<User> findAll() throws Exception {
        return Collections.emptyList();
    }

    @Override
    public User findById(Integer id) throws Exception {
        return null;
    }

}
