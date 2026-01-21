package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    // 생성
    User createUser(String userName);

    // 읽기
    User findById(UUID id);

    // 모두 읽기
    List<User> findAll();

    // 수정
    User updateById(UUID id, User userName);

    // 삭제
    void deleteById(UUID id);

    // 해당 channel Id를 가진 유저 목록을 반환
    List<User> getUsersByChannelId(UUID channelId);

    // 다른 repository setter
    void setChannelRepository(ChannelRepository channelRepository);
    void setMessageRepository(MessageRepository messageRepository);

    // 데이터 저장
    void saveData();

    // 데이터 불러오기
    void loadData();
}
