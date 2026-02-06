package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    public FileChannelRepository(@Value("${discodeit.repository.file-directory:.discodeit}")String directoryPath) {
        super(directoryPath + File.separator + "Channel.ser");
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        Map<UUID, Channel> data = load();
        return data.values().stream()
                .filter(ch -> ch.getUserIds().stream()
                        .anyMatch(uId -> uId.equals(userId)))
                .toList();
    }

    @Override
    public void deleteMessageByMessageId(UUID channelId, UUID messageId) {
        Map<UUID, Channel> data = load();
        Channel channel = data.get(channelId);
        if (channel != null) {
            if (channel.getMessageIds().removeIf(id -> id.equals(messageId))) {
                writeToFile(data);
            }
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, Channel> data = load();
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
                save(ch);
            }
        }
    }
}
