package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;//공개채널에 멤버추가를 위한 의존성
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto create(PublicChannelCreateDto dto) {
        Channel channel = channelMapper.toEntity(dto);
        channelRepository.save(channel);
        //모든 사용자가 멤버!
        userRepository.findAll()
                .forEach(m->readStatusRepository.save(new ReadStatus(m.getId(),channel.getId())));
        return channelToDto(channel);
    }

    @Override
    public ChannelResponseDto create(PrivateChannelCreateDto dto) {
        Channel channel = channelMapper.toEntity(dto);
        channelRepository.save(channel);
        dto.memberIds()
                .forEach(m->readStatusRepository.save(new ReadStatus(m,channel.getId())));
        return channelToDto(channel);
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        return channelToDto(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<ChannelResponseDto> response = new ArrayList<>();
        /*
            사용자가 속한 채널 = 비공개+공개
         */
        readStatusRepository.findAllByUserId(userId)
                .forEach(r->{
                    UUID channelId = r.getChannelId();
                    Channel channel = channelRepository.findById(channelId)
                            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
                    response.add(channelToDto(channel));
                });
        return response;
    }

    @Override
    public ChannelResponseDto update(UUID id,ChannelUpdateDto dto) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
        if(channel.getType()==ChannelType.PRIVATE){
            throw new UnsupportedOperationException("Private channels are not supported");
        }
        channel.update(dto.name(), dto.description());
        return channelToDto(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
    }

    private ChannelResponseDto channelToDto(Channel channel) {
        Message lastMessage = messageRepository.findLastMessageByChannelId(channel.getId())
                .orElse(null);//아직 메세지가 생성 안된경우
        List<UUID> memberIds = new ArrayList<>();
        readStatusRepository
                .findAllByChannelId(channel.getId())
                .forEach(s -> memberIds.add(s.getUserId()));
        return channelMapper.toDto(channel, lastMessage, memberIds);
    }
}
