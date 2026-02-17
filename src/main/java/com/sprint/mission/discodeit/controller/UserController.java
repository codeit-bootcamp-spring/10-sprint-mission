package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> find(@PathVariable UUID id) throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(userService.find(id));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll() throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) throws IOException, ClassNotFoundException {
        return ResponseEntity.status(201).body(userService.create(request));
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<UserResponse> update(@RequestBody UserUpdateRequest request) throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(userService.update(request));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID id) throws IOException, ClassNotFoundException {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserResponse> updateActiveStatus(@PathVariable UUID id) throws IOException, ClassNotFoundException {
        return ResponseEntity.ok(userService.updateActiveAt(id));
    }
}
