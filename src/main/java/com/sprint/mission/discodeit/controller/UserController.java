package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto.response> createUser(@RequestPart("user") UserDto.createRequest userReq,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        BinaryContentDto.createRequest profileReq = saveProfileImage(userReq.accountId(), profileImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userReq, profileReq));
    }

    // 사용자 단일 조회(UUID)
    @RequestMapping(value = "{user-id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto.response> findUser(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUser(userId));
    }

    // 사용자 단일 조회(AccountId)
    @RequestMapping(params = "accountId", method = RequestMethod.GET)
    public ResponseEntity<UserDto.response> findUserByAccountId(@RequestParam String accountId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUserByAccountId(accountId));
    }

    // 사용자 단일 조회(Mail)
    @RequestMapping(params = "mail", method = RequestMethod.GET)
    public ResponseEntity<UserDto.response> findUserByMail(@RequestParam String mail) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUserByMail(mail));
    }

    // 사용자 다중 조회
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto.response>> findUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findAllUsers());
    }

    // 사용자 수정
    @RequestMapping(value = "{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto.response> updateUser(@PathVariable("user-id") UUID userId,
                                                       @RequestPart("user") UserDto.updateRequest userReq,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        BinaryContentDto.createRequest profileReq = saveProfileImage(userReq.accountId(), profileImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.updateUser(userId, userReq, profileReq));
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(value = "{user-id}/updateLastActive", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateLastActive(@PathVariable("user-id") UUID userId) {
        userStatusService.updateUserStatusByUserId(userId,
                new UserStatusDto.updateRequest(Instant.now()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 사용자 삭제
    @RequestMapping(value = "{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static BinaryContentDto.createRequest saveProfileImage(String userAccountId, MultipartFile profileImage) throws IOException {
        BinaryContentDto.createRequest profileReq = null;
        if (profileImage != null) {
            String fileName = userAccountId + "_profileImg" + "." +
                    StringUtils.getFilenameExtension(profileImage.getOriginalFilename());
            Path savePath = Paths.get("resources/static/profiles/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImage.transferTo(savePath);
            profileReq = new BinaryContentDto.createRequest(BinaryContentType.IMAGE, fileName, savePath.toString());
        }
        return profileReq;
    }
}
