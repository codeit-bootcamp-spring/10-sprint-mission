package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    // 저장
    Channel save(Channel channel);

    // 읽기
    Optional<Channel> findById(UUID id);

    // 모두 읽기
    List<Channel> findAll();

    // 수정
//    Channel updateById(UUID id, String newChannelName);

    // 삭제
    void deleteById(UUID id);

//    // 해당 user Id를 가진 유저가 속한 채널 목록을 반환
//    List<Channel> getChannelsByUserId(UUID userId);
    // 서비스 영역으로

//    // 다른 repository setter
//    void setMessageRepository(MessageRepository messageRepository);
//    void setUserRepository(UserRepository userRepository);
    // 다른 레포지토리를 의존하면 안됨

//    // 데이터 로드
//    void loadData();
//    // 데이터 저장
//    void saveData();
    // JCF 레포지토리에서는 필요 없는 기능임(레포지토리 추상화가 잘못된 것)
}
