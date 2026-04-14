package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.util.UUID;

// 클라이언트에게 반환할 유저 정보
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    boolean online

) {

}
