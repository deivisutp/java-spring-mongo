package com.hugosilva.curso.ws.services;

import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.dto.UserDTO;
import com.hugosilva.curso.ws.repository.UserRepository;
import com.hugosilva.curso.ws.services.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException("Object not found"));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User fromDTO(UserDTO userDTO) {
        return new User(userDTO);
    }

    public User update(User user) {
        Optional<User> updateUser= userRepository.findById(user.getId());
        return updateUser.map(item ->
                userRepository.save(new User(   item.getId(),
                                                user.getFirstName(),
                                                user.getLastName(),
                                                user.getEmail())))
                                .orElseThrow(() -> new ObjectNotFoundException("User not found."));
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }
}
