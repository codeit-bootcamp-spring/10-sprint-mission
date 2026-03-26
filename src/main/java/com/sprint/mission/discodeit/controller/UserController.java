package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;


    //전체 사용자 조회
    @RequestMapping(method = RequestMethod.GET)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User 목록 조회 성공",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(
                        implementation = UserDto.class
                    )
                )
            )
        )
    })
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    //사용자 등록
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User가 성공적으로 생성됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(type = "string"),
                examples = @ExampleObject("User with email {email} already exists")
            ))
    })
    public ResponseEntity<UserDto> create(
        @Valid @RequestPart("userCreateRequest") UserCreateRequestDTO userCreateRequestDTO,
        @RequestPart(value = "profile", required = false) MultipartFile profileImage)
        throws IOException {

        BinaryContentDto profileSaved = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileSaved = new BinaryContentDto(
                UUID.randomUUID(),
                profileImage.getOriginalFilename(),
                profileImage.getSize(),
                profileImage.getContentType(),
                profileImage.getBytes()
            );
        }

        UserDto response = userService.create(userCreateRequestDTO, profileSaved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "User가 성공적으로 삭제됨"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("User with id {id} not found")
            )
        )
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return (ResponseEntity.noContent().build());
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("User with id {userId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "같은 email 또는 username을 사용하는 user가 이미 존재함",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("같은 email 또는 username를 사용하는 User가 이미 존재함")
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "User 정보가 성공적으로 수정됨",
            content = @Content(
                schema = @Schema(implementation = User.class)
            )
        )
    })
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID userId,
        @RequestPart("userUpdateRequest") UserUpdateDTO req,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {

        BinaryContentDto profileDto = null;
        if (profile != null && !profile.isEmpty()) {
            profileDto = new BinaryContentDto(
                UUID.randomUUID(),
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType(),
                profile.getBytes()
            );
        }

        return new ResponseEntity<>(userService.update(userId, req, profileDto),
            HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "해당 User의 UserStatus를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("UserStatus with userId {userId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "User 온라인 상태가 성공적으로 업데이트됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserStatus.class)
            )
        )
    }
    )
    public ResponseEntity<UserStatusDto> updateUserOnline(
        @PathVariable UUID userId,
        @RequestBody UserStatusUpdateRequestDTO req) {
        return new ResponseEntity<>(userStatusService.activateUserOnline(userId, req),
            HttpStatus.OK);
    }


}
