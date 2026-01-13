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
    // userName 또는 nickName을 이용한 전체 검색으로 특정 사용자 찾기
    List<User> searchAllUsersByPartialName(String partialName);
    // 특정 사용자가 참여한 모든 채널
    List<Channel> readUserJoinChannelsByUserId(UUID userId);
    // 특정 사용자가 참여한 채널 중에서 특정 채널 검색
    List<Channel> searchUserChannelByChannelName(UUID userId, String partialChannelName);
    // 특정 사용자가 owner인 모든 채널
    List<Channel> readUserOwnChannelsByUserId(UUID userId);
//    // 특정 사용자가 작성한 모든 메시지
//    List<Message> readAllMessageAtUserByUserId(UUID userId);

    // U. 수정
    User updateEmail(UUID requestId, UUID targetId, String email); // 이메일 수정
    User updatePassword(UUID requestId, UUID targetId, String password); // 비밀번호 수정
    User updateNickName(UUID requestId, UUID targetId, String nickName); // 별명 수정
    User updateUserName(UUID requestId, UUID targetId, String userName); // 사용자 이름 수정
    User updateBirthday(UUID requestId, UUID targetId, String birthday); // 생년월일 수정
    User joinChannel(UUID requestId, UUID targetId, Channel channel); // 채널 참여
    User leaveChannel(UUID requestId, UUID targetId, Channel channel); // 채널 탈퇴
    User writeMessage(UUID requestId, UUID targetId, String messageContent, Channel channel); // 메시지 작성

    // D. 삭제
    void deleteUser(UUID requestId, UUID targetId);
}
