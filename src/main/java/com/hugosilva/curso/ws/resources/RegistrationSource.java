package com.hugosilva.curso.ws.resources;

import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.dto.UserDTO;
import com.hugosilva.curso.ws.resources.util.GenericResponse;
import com.hugosilva.curso.ws.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationSource {

    @Autowired
    UserService userService;

    @PostMapping("/public/registration/users")
    public ResponseEntity<Void> registerUser(@RequestBody UserDTO userDTO) {
        User user = this.userService.fromDTO(userDTO);
        this.userService.registerUser(user);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/public/registrationConfirm/users")
    public ResponseEntity<GenericResponse> confirmRegistrationUser(@RequestParam("token") String token) {
        final Object result = this.userService.validateVerificationToken(token);
        if (result == null) return ResponseEntity.ok().body(new GenericResponse("Success"));

        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(new GenericResponse(result.toString()));
    }
}