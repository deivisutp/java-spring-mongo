package com.hugosilva.curso.ws.resources;

import com.hugosilva.curso.ws.domain.Role;
import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.dto.UserDTO;
import com.hugosilva.curso.ws.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserResource {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> users = userService.findAll();
        List<UserDTO> userDto = users.stream().map(item -> new UserDTO(item))
                                    .collect(Collectors.toList());

        return ResponseEntity.ok().body(userDto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> findAll(@PathVariable String id) {
        User user = userService.findById(id);

        return ResponseEntity.ok().body(new UserDTO(user));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
        User user = userService.fromDTO(userDTO);
        return ResponseEntity.ok().body(new UserDTO(userService.create(user)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO userDTO) {
        User user = userService.fromDTO(userDTO);
        user.setId(id);
        return ResponseEntity.ok().body(new UserDTO(userService.update(user)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("users/{id}/roles")
    public ResponseEntity<List<Role>> findRoles(@PathVariable String id) {
        User user = userService.findById(id);

        return ResponseEntity.ok().body(user.getRoles());
    }
}
