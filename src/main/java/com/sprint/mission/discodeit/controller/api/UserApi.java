package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "User 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    ResponseEntity<UserDto.Response> createUser(
            @Parameter(description = "User 생성 정보") @RequestPart("userCreateRequest") UserDto.CreateRequest request,
            @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(summary = "User 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    ResponseEntity<UserDto.Response> updateUser(
            @Parameter(description = "수정할 User ID") @PathVariable("userId") UUID userId,
            @Parameter(description = "수정할 User 정보") @RequestPart("userUpdateRequest") UserDto.UpdateRequest request,
            @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 User ID") @PathVariable("userId") UUID userId
    );

    @Operation(summary = "User 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
    })
    ResponseEntity<UserDto.Response> findUser(
            @Parameter(description = "조회할 User ID") @PathVariable("userId") UUID userId
    );

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UserDto.Response.class)))
    ResponseEntity<List<UserDto.Response>> findAllUser();

    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(schema = @Schema(implementation = UserStatusDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
    })
    ResponseEntity<UserStatusDto.Response> patchUserStatus(
            @Parameter(description = "상태를 변경할 User ID") @PathVariable("userId") UUID userId,
            @RequestBody UserStatusDto.UpdateRequest request
    );

    ResponseEntity<UserDto.Response> updateUserRole(
        @PathVariable("userId") UUID userId,
        @RequestBody @Valid UserRoleUpdateRequest request);
}
