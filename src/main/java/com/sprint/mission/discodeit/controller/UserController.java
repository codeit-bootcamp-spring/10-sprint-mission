package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authdto.AuthDTO;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateResponseDTO;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final AuthService authService;

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "User가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserResponseDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "text/plain",
              schema = @Schema(type = "string"),
              examples = @ExampleObject("User with email {email} already exists")
          )
      )
  })
  public ResponseEntity<UserResponseDTO> postUser(@Valid @RequestBody UserCreateRequestDTO req) {
    UserResponseDTO created = userService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  @ApiResponse(
      responseCode = "200",
      description = "User 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))
      )
  )
  public ResponseEntity<List<UserResponseDTO>> getUsers() {

    return ResponseEntity.ok(userService.findAll());
  }


  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  @ResponseBody
  public void deleteUser(@PathVariable UUID userId) {
    userService.delete(userId);
  }

  @RequestMapping(method = RequestMethod.PATCH)
  @ResponseBody
  public UserResponseDTO updateUser(@RequestBody UserUpdateDTO req) {
    return userService.update(req);
  }

  @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
  @ResponseBody
  public UserStateResponseDTO updateUserOnline(@PathVariable UUID userId) {
    return userStatusService.activateUserOnline(userId);
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ResponseBody
  public UserResponseDTO userLogin(@RequestBody AuthDTO req) {
    return authService.login(req); // 일단 Response DTO만 보내는걸로
    // 추후 로그인 기능을 서비스에서 구현?
  }

}
