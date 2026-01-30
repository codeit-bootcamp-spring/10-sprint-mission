package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public BinaryContent toBinaryContent(BinaryContentCreateRequest request) {
        return new BinaryContent(request.fileName(),
                request.contentType(),
                request.bytes()
        );
    }

    // 생성, 수정 시 reponse DTO 반영하는
    public UserResponse toResponse(User user, boolean online){
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getAlias(),
                user.getEmail(),
                online,
                user.getProfileId()
        );
    }
}
