package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.message.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponse createPrivate(ChannelCreatePrivateRequest request) {
        Channel channel = new Channel(request.type(), request.name(), request.description(),request.ownerId());

        Channel savedChannel = channelRepository.save(channel);

        for(UUID userId : channel.getParticipantUserIds()){
            ReadStatus readStatus = new ReadStatus(userId,channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channelMapper.convertToResponse(savedChannel);
    }

    @Override
    public ChannelResponse createPublic(ChannelCreatePublicRequest request) {
        Channel channel = new Channel(request.type(), request.name(), request.description(),request.ownerId());
        Channel savedChannel = channelRepository.save(channel);
        return channelMapper.convertToResponse(savedChannel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastMessageAt = messageRepository.findByChannelId(channel.getId())
                .stream().map(Message::getUpdatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        ChannelResponse channelResponse;

        if(channel.getType() == ChannelType.PUBLIC){
            channelResponse = new ChannelResponse (
                    channel.getId(),
                    channel.getType().toString(),
                    channel.getName(),
                    channel.getDescription(),
                    lastMessageAt,
                    null
            );
        } else {
            List<ReadStatus> allReadStatuses = readStatusRepository.findAll();

            List<UUID> participantUserIds = new ArrayList<>();
            for (ReadStatus status : allReadStatuses) {
                if (status.getChannelId().equals(channelId)) {
                    participantUserIds.add(status.getUserId());
                }
            }

            channelResponse = new ChannelResponse(
                    channel.getId(),
                    channel.getType().toString(),
                    null,
                    null,
                    lastMessageAt,
                    participantUserIds
            );
        }
        return channelResponse;
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();
        List<ChannelResponse> resultChannels = new ArrayList<>();

        for(Channel channel : allChannels ){
            Instant lastMessageAt = messageRepository.findByChannelId(channel.getId())
                    .stream().map(Message::getCreatedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
             if(channel.getType() == ChannelType.PUBLIC){

                 resultChannels.add (new ChannelResponse(
                         channel.getId(),
                         channel.getType().toString(),
                         channel.getName(),
                         channel.getDescription(),
                         lastMessageAt,
                         null
                 ));
             } else {
                 List<ReadStatus> allReadStatuses = readStatusRepository.findAll();

                 List<UUID> participantUserIds = new ArrayList<>();

                 for (ReadStatus status : allReadStatuses) {
                     if (status.getChannelId().equals(channel.getId())) {
                         participantUserIds.add(status.getUserId());
                     }
                 }
                 if (participantUserIds.contains(userId)){
                 resultChannels.add(new ChannelResponse(
                         channel.getId(),
                         channel.getType().toString(),
                         null,
                         null,
                         lastMessageAt,
                         participantUserIds
                 ));
                 }
             }
        }

        return resultChannels;
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request){
        Channel channel =  channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        ChannelUpdateRequest channelUpdateRequest;

        if(channel.getType() == ChannelType.PRIVATE){
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다");
        }

        channel.update(request.newName(), request.newDescription());
        Channel savedChannel = channelRepository.save(channel);

        return channelMapper.convertToResponse(savedChannel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        List<Message> messages = messageRepository.findByChannelId(channelId);
        for(Message message : messages){
                messageRepository.deleteById(message.getId());
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAll();
        for(ReadStatus readStatus : readStatuses){
            if(readStatus.getChannelId().equals(channelId)){
                readStatusRepository.deleteById(readStatus.getId());
            }
        }
        channelRepository.deleteById(channelId);

    }
}
