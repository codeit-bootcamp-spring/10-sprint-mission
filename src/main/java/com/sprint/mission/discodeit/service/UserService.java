package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;

public interface UserService {
	UserResponseDto create(UserPostDto userPostDTO);

	UserResponseDto findById(UUID userId);

	UserResponseDto findByUserName(String userName); // 이름으로 조회(단건)

	List<UserResponseDto> findAll(); // 전체 조회(다건)

	UserResponseDto updateUser(UUID userId, UserPatchDto userPatchDTO); // 수정하고 싶은 필드에 null 이외의 값을 넣는다.

	void delete(UUID userId);
}
