package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    User createUser(String email, String userName, String nickName, String password, String birthday);

    // R. 읽기
    Optional<User> findUserById(UUID userId);

    // R. 모두 읽기
    // 모든 사용자
    List<User> findAllUsers();
    // 특정 사용자가 참여한 모든 채널
    List<UUID> readJoinChannelIds(UUID userId);
    // 특정 사용자가 작성한 모든 메시지
    List<Message> readUserMessagesByUserId(UUID userId);

    // U. 수정
    User updateUserInfo(UUID userId, String email, String password, String userName, String nickName, String birthday);

    // D. 삭제
    void deleteUser(UUID userId);
}
