package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: userId와 기타 등등 출력
    User createUser(String email, String nickName, String userName, String password, String birthday);

    // R. 읽기
    // 이메일+비번(로그인?)
    Optional<User> readUserByEmailAndPw(String email, String password);
    // 본인?
    Optional<User> readUserById(UUID userId);

    // R. 모두 읽기
    // 모든 사용자
    List<User> readAllUsers();
    // 특정 채널에 속한 모든 유저
    List<User> readAllUsersByChannelId(UUID channelId);
    // 특정 채널에서 특정 사용자들 찾기
    List<User> searchUserAtChannelByChannelIdAndPartialUserName(UUID channelId, String partialUserName);
    // 전체 검색으로 특정 사용자 이름 찾기
    List<User> searchAllUsersByPartialUserName(String partialUserName);

    // U. 수정
    User updateEmail(UUID userId, String email); // 이메일 수정
    User updatePassword(UUID userId, String password); // 비밀번호 수정
    User updateNickName(UUID userId, String nickName); // 별명 수정
    User updateUserName(UUID userId, String userName); // 사용자 이름 수정
    User updateBirthday(UUID userId, String birthday); // 생년월일 수정

    // D. 삭제
    void deleteUser(UUID userId);
}
