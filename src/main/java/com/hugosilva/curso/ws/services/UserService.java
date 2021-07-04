package com.hugosilva.curso.ws.services;

import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.domain.VerificationToken;
import com.hugosilva.curso.ws.dto.UserDTO;
import com.hugosilva.curso.ws.repository.RoleRepository;
import com.hugosilva.curso.ws.repository.UserRepository;
import com.hugosilva.curso.ws.repository.VerificationTokenRepository;
import com.hugosilva.curso.ws.services.exception.ObjectAlreadyExistsException;
import com.hugosilva.curso.ws.services.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElseThrow(() -> new ObjectNotFoundException("Object not found"));
    }

    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
                                                user.getEmail(),
                                                user.getPassword(),
                                                user.isEnabled())))
                                .orElseThrow(() -> new ObjectNotFoundException("User not found."));
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }

    public User registerUser(User user) {
        if (emailExists(user.getEmail())) throw new ObjectAlreadyExistsException(String.format("Email already registered."));

        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER").get()));
        user = create(user);
        return user;
    }

    private boolean emailExists(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) return true;

        return false;
    }

    public void createVerificationTokenForUser(User user, String token) {
        final VerificationToken vToken = new VerificationToken(token, user);
        verificationTokenRepository.save(vToken);
    }

    public String validateVerificationToken(String token) {
        final Optional<VerificationToken> vToken = verificationTokenRepository.findByToken(token);
        if (!vToken.isPresent()) return "invalidToken";

        final User user = vToken.get().getUser();
        final Calendar calendar = Calendar.getInstance();
        if ((vToken.get().getExpireDate().getTime() - calendar.getTime().getTime()) <= 0) {
            return "expiredToken";
        }

        user.setEnabled(true);
        this.userRepository.save(user);
        return null;
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.orElseThrow(() -> new ObjectNotFoundException(String.format("Usuário não encontrado!")));
    }
}
