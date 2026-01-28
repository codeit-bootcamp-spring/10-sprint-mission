package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ChannelDTO.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.DTO.ChannelDTO.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateDTO req) {
        if(channelRepository.findAll()
                .stream()
                .anyMatch(c -> req.name().equals(c.getName())))
        {
            throw new IllegalStateException("해당 채널의 이름이 중복됩니다.");
        }

        Objects.requireNonNull(req.channelType(), "유효하지 않은 채널 타입입니다.");
        Objects.requireNonNull(req.name(), "유효하지 않은 채널 이름입니다.");
        Objects.requireNonNull(req.description(), "유효하지 않은 채널 설명입니다.");

        return channelRepository.save(new Channel(req.channelType(), req.name(), req.description()));
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateDTO req) {
        Channel channel = new Channel(ChannelType.PRIVATE,
                null,
                null);

        Channel saved = channelRepository.save(channel);

        req.users()
                .forEach(u -> {
                    userRepository.findById(u.getId()).orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));
                    ReadStatus readStatus = new ReadStatus(u.getId(),
                            saved.getId()
                    );
                    readStatusRepository.save(readStatus);
                });

        return saved;
    }


    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
    }
}
