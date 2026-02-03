package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("channelService")
public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepository;
    private ReadStatusRepository readStatusRepository;
    private MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository, MessageRepository messageRepository, ReadStatusRepository readStatusRepository, HandlerMapping defaultServletHandlerMapping) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
    }

    @Override
    public ChannelDTO createPrivateChannel(ChannelCreatePrivateDTO channelCreateDTO) {
        Channel channel;
        channel = new Channel(channelCreateDTO.userList(), channelCreateDTO.messageList());

        for (UUID userId : channelCreateDTO.userList()) {
            ReadStatus readStatus = new ReadStatus(channel.getId(), userId);
            this.readStatusRepository.save(readStatus);
        }

        this.channelRepository.save(channel);

        return (channel.isPrivate()) ? createPrivateChannelDTO(channel) : createPublicChannelDTO(channel);
    }

    @Override
    public ChannelDTO createPublicChannel(ChannelCreatePublicDTO channelCreateDTO) {
        Channel channel;
        channel = new Channel(channelCreateDTO.channelName(), channelCreateDTO.description());
        this.channelRepository.save(channel);

        return createPublicChannelDTO(channel);
    }

    @Override
    public ChannelDTO findById(UUID channelId) {
        Channel channel = this.channelRepository.loadById(channelId);

        if (channel.isPrivate()) {
            return createPrivateChannelDTO(channel);
        } else {
            return createPublicChannelDTO(channel);
        }
    }

    @Override
    public List<ChannelDTO> findAll() {
        List<ChannelDTO> channelDTOList = new ArrayList<>();

        for (Channel channel : this.channelRepository.loadAll()) {
            if (channel.isPrivate()) {
                channelDTOList.add(createPrivateChannelDTO(channel));
            } else {
                channelDTOList.add(createPublicChannelDTO(channel));
            }
        }

        return channelDTOList;
    }

    @Override
    public List<ChannelDTO> findAllByUserId(UUID userId) {
        List<ChannelDTO> channelDTOList = new ArrayList<>();
        List<Channel> channelList = channelRepository.loadAll();

        for (Channel channel : channelList) {
            if (channel.isPrivate() && channel.getId().equals(userId)) {
                channelDTOList.add(createPrivateChannelDTO(channel));
            } else if (!channel.isPrivate() && channel.getId().equals(userId)){
                channelDTOList.add(createPublicChannelDTO(channel));
            }
        }

        return channelDTOList;
    }

    @Override
    public ChannelDTO updateChannelname(ChannelUpdateDTO channelUpdateDTO) {
        Channel channel = this.channelRepository.loadById(channelUpdateDTO.channelId());

        if (!channelUpdateDTO.name().isEmpty()) {
            channel.updateChannelName(channelUpdateDTO.name());
        }
        if (!channelUpdateDTO.description().isEmpty()) {
            channel.updateChannelDescription(channelUpdateDTO.description());
        }

        return (channel.isPrivate()) ? createPrivateChannelDTO(channel) : createPublicChannelDTO(channel);
    }

    @Override
    public void delete(UUID channelId) {
        this.readStatusRepository.deleteByChannelId(channelId);
        this.messageRepository.deleteByChannelId(channelId);
        this.channelRepository.delete(channelId);
    }

//    public ChannelDTO createChannelDTO(Channel channel) {
//        return new ChannelDTO(
//                channel.getId(),
//                channel.getChannelName(),
//                channel.getDescription(),
//                channel.isPrivate(),
//                channel.getUserList(),
//                channel.getMessagesList(),
//                this.getLastMessageTime(channel.getId())
//        );
//    }

    public ChannelDTO createPrivateChannelDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.isPrivate(),
                channel.getUserList(),
                channel.getMessagesList(),
                this.getLastMessageTime(channel.getId())
        );
    }

    public ChannelDTO createPublicChannelDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.isPrivate(),
                new ArrayList<>(),
                channel.getMessagesList(),
                this.getLastMessageTime(channel.getId())
        );
    }

    public Instant getLastMessageTime(UUID channelId) {
        Instant lastMessageTime = Instant.MIN;

        for (ReadStatus readStatus : this.readStatusRepository.loadAllByChannelId(channelId)) {
            if (readStatus.getUpdatedAt().isAfter(lastMessageTime)) {
                lastMessageTime = readStatus.getUpdatedAt();
            }
        }

        return lastMessageTime;
    }

    // 유저 참가
    public void joinUser(ChannelUserJoinDTO channelUserJoinDTO) {
        channelUserJoinDTO.user().getChannelList().add(channelUserJoinDTO.channelId());
        this.findById(channelUserJoinDTO.channelId()).userList().add(channelUserJoinDTO.user().getId());
    }

    // 유저 탈퇴
    public void quitUser(ChannelUserQuitDTO channelUserQuitDTO) {
        User user = channelUserQuitDTO.user();
        Channel channel = this.channelRepository.loadById(channelUserQuitDTO.channelId());
        user.getChannelList().remove(channel);
        channel.getUserList().remove(user);
    }

    // 특정 유저가 참가한 채널 리스트 조회
    public List<UUID> readUserChannelList(UUID userId) {
        List<UUID> channelList = new ArrayList<>();

        for (ReadStatus readStatus : this.readStatusRepository.loadAll()) {
            if (readStatus.getUserId().equals(userId)) {
                channelList.add(readStatus.getChannelId());
            }
        }

        return channelList;
    }
}