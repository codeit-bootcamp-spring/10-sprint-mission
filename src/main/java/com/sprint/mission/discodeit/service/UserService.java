package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDto create(UserPostDto userPostDTO, MultipartFile profile);

    UserDto findById(UUID userId);

    UserDto findByUsername(String username); // 이름으로 조회(단건)

    List<UserDto> findAll(); // 전체 조회(다건)

    UserDto updateUser(UUID userId,
        UserPatchDto userPatchDTO, MultipartFile profile);

    void delete(UUID userId);
}
