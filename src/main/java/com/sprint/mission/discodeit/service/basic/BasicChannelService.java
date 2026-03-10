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
        Objects.requireNonNull(req.name(), "채널 이름은 필수입니다!");
        Objects.requireNonNull(req.description(), "채널 설명은 필수입니다!");

        if (channelRepository.existsByName(req.name())) {
            throw new IllegalStateException("채널 이름이 중복됩니다.");
        }

        // 채널 객체 생성
        Channel channel = new Channel(ChannelType.PUBLIC, req.name(), req.description());

        // Transactional 에서 CREATE 작업 시에는 save나 persist 등 영속 메서드 호출이 필수.
        // 채널 검증과 save의 원자성을 보장하기 위해 아래와 같이 코드 작성
        try {
            Channel saved = channelRepository.save(channel);
            return channelMapper.toDto(saved, null);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("채널 이름이 중복됩니다.");
        }

    }

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
        Objects.requireNonNull(req.users(), "유효하지 않은 참가자 명단!");

        if (req.users().isEmpty()) {
            throw new IllegalStateException("User list is empty");
        }

        Channel channel = new Channel(ChannelType.PRIVATE);
        Channel saved = channelRepository.save(channel);

        // req에서 유저 리스트를 뽑아낸 뒤,
        // 각 유저들마다 ReadStatus 생성
        req.users().forEach(u -> {
            ReadStatus rs = new ReadStatus(userRepository.findById(u)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다.")), saved);
            readStatusRepository.save(rs);
        });

        return channelMapper.toDto(saved, null);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("찾을 수없음"));
        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "유효하지 않은 식별자입니다!");

        List<Channel> channels = channelRepository.findAllParticipatingWithParticipants(userId);

        List<UUID> channelIds = channels.stream()
            .map(Channel::getId)
            .toList();

        // Channel Mapper에 채널 UUID와 마지막 메시지를 전달하기 위해 Map 형태로 변환.
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
        Objects.requireNonNull(channelId, "유효하지 않은 채널 식별자!");
        Objects.requireNonNull(req, "유효하지 않은 요청!");
        Objects.requireNonNull(req.newName(), "유효하지 않은 채널명!");
        Objects.requireNonNull(req.newDescription(), "유효하지 않은 채널 설명!");

        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("해당 채널을 찾을 수 없습니다!"));

        channel.update(req.newName(), req.newDescription()); // dirty-checking

        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        channelRepository.delete(channel);
    }
}
