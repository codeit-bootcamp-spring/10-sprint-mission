package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    //특정채널내 가장 최근 메시지시간
    Instant findLstMessageTimeByChannelId(UUID channelId);
    //특정채널내 메시지
    void deleteAllMessagesByChannelId(UUID channelId);
    List<Message>findByChannelId(UUID channelId);
}
