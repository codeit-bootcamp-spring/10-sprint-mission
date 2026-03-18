package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateDTO req) {
        Objects.requireNonNull(req.name(), "梨꾨꼸 ?대쫫? ?꾩닔?낅땲??");
        Objects.requireNonNull(req.description(), "梨꾨꼸 ?ㅻ챸? ?꾩닔?낅땲??");

        if (channelRepository.existsByName(req.name())) {
            throw new IllegalStateException("梨꾨꼸 ?대쫫??以묐났?⑸땲??");
        }
        
        Channel channel = new Channel(ChannelType.PUBLIC, req.name(), req.description());

        try {
            Channel saved = channelRepository.save(channel);
            return channelMapper.toDto(saved, null);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("梨꾨꼸 ?대쫫??以묐났?⑸땲??");
        }

    }

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateDTO req) {
        if (req == null || req.users() == null) {
            throw new IllegalStateException("Invalid private channel request");
        }

        List<UUID> participantIds = req.users().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        if (participantIds.isEmpty()) {
            throw new IllegalStateException("User list is empty");
        }

        String privateChannelName = "private-" + UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PRIVATE, privateChannelName, "private channel");
        Channel saved = channelRepository.save(channel);

        participantIds.forEach(u -> {
            ReadStatus rs = new ReadStatus(userRepository.findById(u)
                .orElseThrow(() -> new NoSuchElementException("User not found")), saved);
            readStatusRepository.save(rs);
        });

        Channel savedWithParticipants = channelRepository.findWithParticipantsById(saved.getId())
            .orElse(saved);

        return channelMapper.toDto(savedWithParticipants, null);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("찾을 수 없는 유저"));
        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "?좏슚?섏? ?딆? ?앸퀎?먯엯?덈떎!");

        List<Channel> channels = channelRepository.findAllVisibleWithParticipants(
            userId, ChannelType.PUBLIC);

        List<UUID> channelIds = channels.stream()
            .map(Channel::getId)
            .toList();

        // Channel Mapper??梨꾨꼸 UUID? 留덉?留?硫붿떆吏瑜??꾨떖?섍린 ?꾪빐 Map ?뺥깭濡?蹂??
        Map<UUID, Message> lastMessageMap = messageRepository.findLastMessageByChannelIds(
                channelIds)
            .stream()
            .collect(Collectors.toMap(
                m -> m.getChannel().getId(),
                m -> m,
                (m1, m2) -> m1
            ));

        return channels.stream()
            .map(channel -> channelMapper.toDto(
                channel, lastMessageMap.get(channel.getId())
            )).toList();
    }


    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequestDTO req) {
        Objects.requireNonNull(channelId, "?좏슚?섏? ?딆? 梨꾨꼸 ?앸퀎??");
        Objects.requireNonNull(req, "?좏슚?섏? ?딆? ?붿껌!");
        Objects.requireNonNull(req.newName(), "?좏슚?섏? ?딆? 梨꾨꼸紐?");
        Objects.requireNonNull(req.newDescription(), "?좏슚?섏? ?딆? 梨꾨꼸 ?ㅻ챸!");

        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("?대떦 梨꾨꼸??李얠쓣 ???놁뒿?덈떎!"));

        channel.update(req.newName(), req.newDescription()); // dirty-checking

        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional
    public List<ChannelDto> findAll() {
        List<Channel> channels = channelRepository.findAll();
        List<UUID> channelIds = channels.stream().map(Channel::getId).toList();
        List<Message> lastMessages = messageRepository.findLastMessageByChannelIds(
            channels.stream().map(Channel::getId).toList());

        // Channel Mapper??梨꾨꼸 UUID? 留덉?留?硫붿떆吏瑜??꾨떖?섍린 ?꾪빐 Map ?뺥깭濡?蹂??
        Map<UUID, Message> lastMessageMap = messageRepository.findLastMessageByChannelIds(
                channelIds)
            .stream()
            .collect(Collectors.toMap(
                m -> m.getChannel().getId(),
                m -> m,
                (m1, m2) -> m1
            ));

        return channels.stream()
            .map(channel -> channelMapper.toDto(
                channel, lastMessageMap.get(channel.getId())
            )).toList();
    }


    @Transactional
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("??젣??梨꾨꼸??議댁옱?섏? ?딆뒿?덈떎."));
        channelRepository.delete(channel);
    }
}
