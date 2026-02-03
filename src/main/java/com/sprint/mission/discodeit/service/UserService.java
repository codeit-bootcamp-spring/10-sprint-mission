package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.UserPatchDTO;
import com.sprint.mission.discodeit.dto.UserPostDTO;
import com.sprint.mission.discodeit.dto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
	User create(UserPostDTO userPostDTO);

	UserResponseDTO findById(UUID userId);

	UserResponseDTO findByUserName(String userName); // 이름으로 조회(단건)

	List<UserResponseDTO> findAll(); // 전체 조회(다건)

	User updateUser(UserPatchDTO userPatchDTO); // 수정하고 싶은 필드에 null 이외의 값을 넣는다.

	void delete(UUID userId);
}
