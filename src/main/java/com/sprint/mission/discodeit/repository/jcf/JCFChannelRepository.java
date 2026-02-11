package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository(){
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(ch -> ch.getUserIds().stream()
                        .anyMatch(uid -> uid.equals(userId)))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());

    }



    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteMessageByMessageId(UUID channelId, UUID messageId) {
        Channel channel = data.get(channelId);
        if (channel != null) {
            channel.getMessageIds().removeIf(id -> id.equals(messageId));
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        // 사용자가 등록되어 있는 채널들
        List<Channel> joinedChannels = data.values().stream()
                .filter(ch -> ch.getUserIds().stream()
                        .anyMatch(uId -> uId.equals(userId)))
                .toList();

        for (Channel ch : joinedChannels) {
            // 내가 방장인 채널 - 채널 자체 삭제
            if (ch.getOwnerId().equals(userId)) {
                delete(ch.getId());
            }
            // 참여한 채널 - 멤버 명단에서 나만 삭제
            else {
                ch.getUserIds().removeIf(uId -> uId.equals(userId));
            }
        }
    }

    @Override
    public void clear(){
        this.data.clear();
    }
}
