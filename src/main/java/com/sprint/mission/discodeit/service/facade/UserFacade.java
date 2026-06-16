package com.sprint.mission.discodeit.service.facade;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.etc.InvalidFileTypeException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 웹 계층(Controller)과 도메인 계층(Service) 사이에서
 * 파일 처리와 유저 생성 트랜잭션을 하나로 묶어주는 퍼사드 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserFacade {

  private final UserService userService;
  private final BinaryContentService binaryContentService;

  @Transactional
  public UserDto.Response createUser(UserDto.CreateRequest request, MultipartFile profile) {
    UUID profileId = uploadProfile(profile);
    return userService.create(request, profileId);
  }

  @Transactional
  public UserDto.Response updateUser(UUID userId, UserDto.UpdateRequest request, MultipartFile profile) {
    UUID profileId = uploadProfile(profile);
    return userService.update(userId, request, profileId);
  }

  private UUID uploadProfile(MultipartFile file) {
    // 파일이 null이거나 비어있으면 null 반환
    if (file == null || file.isEmpty()) {
      return null;
    }

    if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
      throw InvalidFileTypeException.imageOnly(file.getContentType());
    }

    return binaryContentService.create(binaryContentService.multipartFileToCreateRequest(file)).id();
  }
}
