package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name="User", description = "User API")
public class UserController {
    private final UserService userService;

    // user 등록 - POST /api/users
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 등록", operationId = "create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User 생성 실패",
                    content = @Content(examples = @ExampleObject("username 혹은 email이 중복됩니다"))
            )
    })
    public ResponseEntity<UserDTO> postUser(@Valid @RequestPart("userCreateRequest") UserCreateRequest request,
                            @RequestPart(value="profile", required = false) MultipartFile profile){
        log.info("사용자 생성 요청 - username: {}, email: {}", request.username(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request, Optional.ofNullable(profile)));
    }

    // user 정보 수정 - PATCH /api/users/{userId}
    @PatchMapping(value="/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 정보 수정", operationId = "update")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보 수정 완료",
                    content = @Content(
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User 정보 수정 실패",
                    content = @Content(examples = @ExampleObject("username 혹은 email이 중복됩니다"))
            )
    })
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(
                    description = "수정할 userId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value="profile", required = false) MultipartFile profile
    ){
        log.info("사용자 수정 요청 - userId: {}", userId);
        return ResponseEntity.ok(userService.update(userId, request, Optional.ofNullable(profile)));
    }

    // user 삭제 - DELETE /api/users/{userId}
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "User 삭제", operationId = "delete")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User 삭제 실패",
                    content = @Content(examples = @ExampleObject("해당 User를 찾지 못함"))
            )
    })
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
    public void deleteUser(
            @Parameter(
                    description = "삭제할 userId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID userId
    ){
        log.info("사용자 삭제 요청 - userId: {}", userId);
        userService.deleteUser(userId);
    }

    // user 단건 조회
    @GetMapping("/{userId}")
    @Operation(summary = "User 단건 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 단건 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User 단건 조회 실패",
                    content = @Content(examples = @ExampleObject("해당 User를 찾을 수 없음"))
            )
    })
    public ResponseEntity<UserDTO> getUser(
            @Parameter(
                    description = "조회할 userId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID userId
    ){
        log.debug("사용자 단건 조회 요청 - userId: {}", userId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(userService.find(userId));
    }

    // user 다건 조회 - GET /api/users
    @GetMapping
    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @ApiResponse(
            responseCode = "200",
            description = "전체 user 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))
    )
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        log.debug("전체 사용자 목록 조회 요청");
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
}
