package com.hugosilva.curso.ws.services;

import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.domain.VerificationToken;
import com.hugosilva.curso.ws.dto.UserDTO;
import com.hugosilva.curso.ws.repository.RoleRepository;
import com.hugosilva.curso.ws.repository.UserRepository;
import com.hugosilva.curso.ws.repository.VerificationTokenRepository;
import com.hugosilva.curso.ws.services.email.EmailService;
import com.hugosilva.curso.ws.services.exception.ObjectAlreadyExistsException;
import com.hugosilva.curso.ws.services.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private EmailService emailService;

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
        user.setEnabled(false);
        user = create(user);
        this.emailService.sendConfirmationHtmlEmail(user, null, 0);

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

    public VerificationToken generateNewVerificationToken(String email, int select) {
        User user = this.findByEmail(email);
        VerificationToken newToken;
        Optional<VerificationToken> vToken = verificationTokenRepository.findByUser(user);
        if (vToken.isPresent()) {
            vToken.get().updateToken(UUID.randomUUID().toString());
            newToken = vToken.get();
        } else {
            newToken = new VerificationToken(UUID.randomUUID().toString(), user);
        }

        VerificationToken updateVToken = verificationTokenRepository.save(newToken);
        emailService.sendConfirmationHtmlEmail(user, updateVToken, select);
        return updateVToken;
    }

    public String validatePasswordResetToke(String idUser, String token) {
        final Optional<VerificationToken> vToken = verificationTokenRepository.findByToken(token);
        if (!vToken.isPresent()) {
            return "invalidToken";
        }

        if (!vToken.get().getUser().getId().equals(idUser)) {
            return "invalidToken";
        }

        final Calendar cal = Calendar.getInstance();
        if ((vToken.get().getExpireDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "expiredToken";
        }

        return null;
    }

    public VerificationToken getVerificationTokenByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Token not found.")));
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
