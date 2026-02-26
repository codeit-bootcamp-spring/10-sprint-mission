package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserResponseDto create(UserPostDto userPostDTO, MultipartFile profile);

  UserResponseDto findById(UUID userId);

  UserResponseDto findByUsername(String username); // 이름으로 조회(단건)

  List<UserResponseDto> findAll(); // 전체 조회(다건)

  UserResponseDto updateUser(UUID userId,
      UserPatchDto userPatchDTO, MultipartFile profile);

  void delete(UUID userId);
}
