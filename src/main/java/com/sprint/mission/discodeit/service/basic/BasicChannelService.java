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
        channel = new Channel();

        ReadStatus readStatus = new ReadStatus(channel.getId(), channelCreateDTO.user().getId());

        this.channelRepository.save(channel);
        this.readStatusRepository.save(readStatus);

        ChannelDTO channelDTO = new ChannelDTO(channel.getId(), channel);
        return channelDTO;
    }

    @Override
    public ChannelDTO createPublicChannel(ChannelCreatePublicDTO channelCreateDTO) {
        Channel channel;
        channel = new Channel(channelCreateDTO.channelName(), channelCreateDTO.description());
        this.channelRepository.save(channel);

        ChannelDTO channelDTO = new ChannelDTO(channel.getId(), channel);
        return channelDTO;
    }

    @Override
    public ChannelFindDTO findById(UUID id) {
        ChannelFindDTO channelFindDTO;
        Channel channel = this.channelRepository.loadById(id);
        Instant lastMessageTime = this.readStatusRepository.loadById(id).getUpdatedAt();
        List<UUID> userList = new ArrayList<>();
        if (channel.isPrivate()) {
            userList = channel.getUserList();
        }
        channelFindDTO = new ChannelFindDTO(channel, lastMessageTime, userList);

        return channelFindDTO;
//        for (Channel channelCheck : this.channelRepository.loadAll()) {
//            if (channelCheck.getId().equals(id)) {
//                return channelFindDTO;
//            }
//        }
//        throw new NoSuchElementException();
    }

    @Override
    public ChannelFindAllDTO findAll() {
        ChannelFindAllDTO channelFindAllDTO;
        List<ChannelFindDTO> publicChannelList = new ArrayList<>();
        List<ChannelFindDTO> privateChannelList = new ArrayList<>();
        List<Channel> channelList = channelRepository.loadAll();

        for (Channel channel : channelList) {
            if (channel.isPrivate()) {
                ChannelFindDTO channelFindDTO = new ChannelFindDTO(channel,
                        this.readStatusRepository.loadById(channel.getId()).getUpdatedAt(),
                        channel.getUserList());
                privateChannelList.add(channelFindDTO);
            } else {
                ChannelFindDTO channelFindDTO = new ChannelFindDTO(channel,
                        this.readStatusRepository.loadById(channel.getId()).getUpdatedAt(),
                        new ArrayList<>());
                publicChannelList.add(channelFindDTO);
            }
        }

        channelFindAllDTO = new ChannelFindAllDTO(publicChannelList, privateChannelList);
        return channelFindAllDTO;
    }

    public ChannelFindAllDTO findAllByUserId(UUID userId) {
        ChannelFindAllDTO channelFindAllDTO;
        List<ChannelFindDTO> publicChannelList = new ArrayList<>();
        List<ChannelFindDTO> privateChannelList = new ArrayList<>();
        List<Channel> channelList = channelRepository.loadAll();

        for (Channel channel : channelList) {
            if (channel.isPrivate() && channel.getId().equals(userId)) {
                ChannelFindDTO channelFindDTO = new ChannelFindDTO(channel,
                        this.readStatusRepository.loadById(channel.getId()).getUpdatedAt(),
                        channel.getUserList());
                privateChannelList.add(channelFindDTO);
            } else if (!channel.isPrivate()){
                ChannelFindDTO channelFindDTO = new ChannelFindDTO(channel,
                        this.readStatusRepository.loadById(channel.getId()).getUpdatedAt(),
                        new ArrayList<>());
                publicChannelList.add(channelFindDTO);
            }
        }

        channelFindAllDTO = new ChannelFindAllDTO(publicChannelList, privateChannelList);
        return channelFindAllDTO;
    }

    @Override
    public ChannelDTO updateChannelname(ChannelUpdateDTO channelUpdateDTO) {
        if (!channelUpdateDTO.name().isEmpty()) {
            this.findById(channelUpdateDTO.channelId()).channel().updateChannelName(channelUpdateDTO.name());
        }
        if (!channelUpdateDTO.description().isEmpty()) {
            this.findById(channelUpdateDTO.channelId()).channel().updateChannelDescription(channelUpdateDTO.description());
        }

        ChannelDTO channelDTO = new ChannelDTO(this.findById(channelUpdateDTO.channelId()).channel().getId(), this.findById(channelUpdateDTO.channelId()).channel());
        return channelDTO;
    }

    @Override
    public void delete(UUID channelId) {
        this.readStatusRepository.deleteByChannelId(channelId);
        this.messageRepository.deleteByChannelId(channelId);
        this.channelRepository.delete(channelId);
    }

    // 유저 참가
    public void joinUser(ChannelUserJoinDTO channelUserJoinDTO) {
        channelUserJoinDTO.user().getChannelList().add(channelUserJoinDTO.channelId());
        this.findById(channelUserJoinDTO.channelId()).userList().add(channelUserJoinDTO.user().getId());
    }

    // 유저 탈퇴
    public void quitUser(ChannelUserQuitDTO channelUserQuitDTO) {
        User user = channelUserQuitDTO.user();
        Channel channel = this.findById(channelUserQuitDTO.channelId()).channel();
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