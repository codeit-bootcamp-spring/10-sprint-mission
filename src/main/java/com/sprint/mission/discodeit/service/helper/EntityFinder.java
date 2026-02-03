package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.*;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class EntityFinder {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    public EntityFinder(UserRepository userRepository,
                        ChannelRepository channelRepository,
                        MessageRepository messageRepository,
                        UserStatusRepository userStatusRepository,
                        BinaryContentRepository binaryContentRepository,
                        ReadStatusRepository readStatusRepository
                        ) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.readStatusRepository = readStatusRepository;
    }

    public User getUser(UUID userId) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
    }

    public Channel getChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");
        return channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException("해당 채널이 존재하지 않습니다."));
    }

    public Message getMessage(UUID messageId) {
        Objects.requireNonNull(messageId,"메세지 Id가 유효하지 않습니다.");
        return messageRepository.findById(messageId).orElseThrow(() -> new NotFoundException("해당 메세지가 존재하지 않습니다."));
    }

    public UserStatus getStatusByUser(UUID userId) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        return userStatusRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("해당 유저 상태 정보가 존재하지 않습니다."));
    }

    public BinaryContent getBinaryContent(UUID binaryContentId) {
        Objects.requireNonNull(binaryContentId, "자료 Id가 유효하지 않습니다.");
        return binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new NotFoundException("해당 자료 정보가 존재하지 않습니다."));
    }

    public ReadStatus getReadStatus(UUID readStatusId) {
        Objects.requireNonNull(readStatusId,"읽음 상태 Id가 유효하지 않습니다.");
        return readStatusRepository.findById(readStatusId).orElseThrow(() -> new NotFoundException("해당 읽음 상태 객체가 존재하지 않습니다."));
    }
}
