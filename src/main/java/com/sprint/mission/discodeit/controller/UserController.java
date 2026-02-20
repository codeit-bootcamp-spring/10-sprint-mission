package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserSummaryResponseDTO;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    
    // 사용자 등록
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<UserSummaryResponseDTO> create(@RequestPart("userCreateRequestDTO") UserCreateRequestDTO userCreateRequestDTO,
                                                         @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Optional<BinaryContentCreateRequestDTO> profileImageDTO = toBinaryContentCreateRequestDTO(profileImage);
        UserSummaryResponseDTO response = userService.create(userCreateRequestDTO, profileImageDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // 사용자 정보 수정
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<UserSummaryResponseDTO> update(@PathVariable("user-id") UUID userId,
                                                         @RequestPart("userUpdateRequestDTO") UserUpdateRequestDTO userUpdateRequestDTO,
                                                         @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Optional<BinaryContentCreateRequestDTO> profileImageDTO = toBinaryContentCreateRequestDTO(profileImage);
        UserSummaryResponseDTO response = userService.update(userId, userUpdateRequestDTO, profileImageDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 정보 삭제
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    // 특정 사용자 조회
    @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserDetailResponseDTO> find(@PathVariable("user-id") UUID userId) {
        UserDetailResponseDTO response = userService.find(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // 모든 사용자 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserDetailResponseDTO>> findAll() {
        List<UserDetailResponseDTO> response = userService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 온라인 상태 업데이트 -> UserStatusService를 필드로?
    @RequestMapping(value = "/{user-id}/userstatus", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<UserStatusResponseDTO> updateUserStatusByUserId(@PathVariable("user-id") UUID userId,
                                                                          @RequestBody UserStatusUpdateRequestDTO userStatusUpdateRequestDTO) {
        UserStatusResponseDTO response = userStatusService.updateByUserId(userId, userStatusUpdateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 유저 생성, 수정 시 입력 받은 프로필 이미지를 Service에 전달하기 전 Optional<BinaryContentCreateRequestDTO>로 변환하는 private 메서드
    private Optional<BinaryContentCreateRequestDTO> toBinaryContentCreateRequestDTO(MultipartFile file) {
        return Optional.ofNullable(file)
                .map(f -> {
                    try {
                        return new BinaryContentCreateRequestDTO(
                                f.getOriginalFilename(),
                                f.getBytes(),
                                f.getContentType()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
