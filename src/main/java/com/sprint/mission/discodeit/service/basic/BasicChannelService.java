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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
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

        // 채널 생성 메서드 시작 로그
        log.trace("공개 채널 생성 메서드 시작: channelname={}", req.name());

        if (channelRepository.existsByName(req.name())) {
            log.warn("채널 이름 중복: channelName={}", req.name());
            throw new IllegalStateException("채널명이 중복됩니다.");
        }

        // 채널 객체 생성 (영속화는 아직)
        Channel channel = new Channel(ChannelType.PUBLIC, req.name(), req.description());

        // 생성된 채널 객체 정보 로그
        log.debug("생성된 공개 채널 객체 정보: channelId={}", channel.getId());

        // 생성된 채널 객체 영속화 및 dto 변환 리턴 시도
        log.trace("생성된 공개 채널 영속화 시도: channelId={}", channel.getId());
        try {
            Channel saved = channelRepository.save(channel);
            log.info("공개 채널 생성 및 영속화 완료: channelName={}", channel.getName());
            return channelMapper.toDto(saved, null);
        } catch (DataIntegrityViolationException e) {
            log.warn("채널 영속화 실패: channelName={}", channel.getName());
            throw new IllegalStateException("채널이 DB 제약을 해칩니다.");
        }
    }

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateDTO req) {
        // 사설 채널 생성 메서드 시작 로그
        log.trace("사설 채널 생성 메서드 시작");

        if (req == null || req.users() == null) {
            throw new IllegalStateException("사설 채널 생성 요청이 유효하지 않습니다.");
        }

        // 사설 채널 유저 리스트를 생성 (Null 요소 제외 및 중복 유저 제거)
        List<UUID> participantIds = req.users().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        // 유저 리스트가 비어있으면 예외 발생
        if (participantIds.isEmpty()) {
            throw new IllegalStateException("유저 리스트가 비어있음");
        }

        log.trace("사설 채널 객체 생성 시도");
        String privateChannelName = "private-" + UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PRIVATE, privateChannelName, "private channel");
        log.debug("생성된 사설 채널 객체 정보: channelId={}", channel.getId());

        log.trace("생성된 사설 채널 객체 영속화 시도: channelId={}", channel.getId());
        Channel saved = channelRepository.save(channel);
        log.debug("사설 채널 객체 영속화 정보: channelId={}", saved.getId());

        log.trace("유저 리스트 순회하며 ReadStatus 최신화 시도");
        participantIds.forEach(u -> {
            ReadStatus rs = new ReadStatus(userRepository.findById(u)
                .orElseThrow(() -> new NoSuchElementException("User not found")), saved);
            readStatusRepository.save(rs);
        });

        // ReadStatus가 최신화된 채널을 다시 영속화, 변동사항 없으면 이전에 영속화했던 saved를 대입
        Channel savedWithParticipants = channelRepository.findWithParticipantsById(saved.getId())
            .orElse(saved);

        log.info("사설 채널 생성 및 영속화 성공");

        return channelMapper.toDto(savedWithParticipants, null);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.trace("채널 조회 메서드 시작: channelId={}", channelId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("찾을 수 없는 유저"));
        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        log.info("채널 조회 성공: channelName={}", channel.getName());
        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.trace("유저 ID를 통해 채널 조회 메서드 시작: userId={}", userId);

        Objects.requireNonNull(userId, "유효하지 않은 사용자 ID 입니다!");

        List<Channel> channels = channelRepository.findAllVisibleWithParticipants(
            userId, ChannelType.PUBLIC);

        List<UUID> channelIds = channels.stream()
            .map(Channel::getId)
            .toList();

        Map<UUID, Message> lastMessageMap = messageRepository.findLastMessageByChannelIds(
                channelIds)
            .stream()
            .collect(Collectors.toMap(
                m -> m.getChannel().getId(),
                m -> m,
                (m1, m2) -> m1
            ));

        List<ChannelDto> result = channels.stream()
            .map(channel -> channelMapper.toDto(
                channel, lastMessageMap.get(channel.getId())
            )).toList();

        log.info("채널 조회 성공: channelNames={}", result
            .stream()
            .map(ChannelDto::name)
            .toList()
        );

        return result;
    }


    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequestDTO req) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널 ID 입니다!");
        Objects.requireNonNull(req, "유효하지 않은 채널 수정 요청입니다!");

        // 채널 업데이트 메서드 시작 로그
        log.trace("채널 업데이트 메서드 시작: channelId={}, newName={}, newDescription={}",
            channelId, req.newName(), req.newDescription());

        // 채널 레포지토리에서 수정하고자 하는 채널이 존재하는지 조회
        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("해당 채널이 존재하지 않습니다!"));

        // 채널 엔티티의 update 메서드를 통해 수정
        channel.update(req.newName(), req.newDescription()); // dirty-checking

        // 수정하고자 하는 채널의 가장 최신 메시지를 찾음.
        Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

        log.info("채널 수정 완료: channelNewName={}, channelNewDescription={}",
            channel.getName(), channel.getDescription());

        return channelMapper.toDto(channel, lastMessage);
    }

    @Transactional
    public List<ChannelDto> findAll() {
        // 공개 채널 전체 조회 메서드 시작 로그
        log.trace("채널 전체 조회 메서드 시작");

        // 채널 레포지토리에서 전체 채널 조회
        List<Channel> channels = channelRepository.findAll();

        // 조회한 채널 리스트에서 id만 추출하여 리스트화
        List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

        // 메세지 레포지토리에서 채널 ID를 사용하여 각 채널 당 최신 메시지를 추출 및 리스트화
        List<Message> lastMessages = messageRepository.findLastMessageByChannelIds(
            channels.stream().map(Channel::getId).toList());

        // Map 자료구조를 사용,
        Map<UUID, Message> lastMessageMap = lastMessages
            .stream()
            .collect(Collectors.toMap( // 최신 메시지 리스트를 Map 구조로 변환
                m -> m.getChannel().getId(), // 메시지 별 채널 Id가 Key
                m -> m, // 해당 최신 메시지가 Value가 됨.
                (m1, m2) -> m1
            ));

        log.info("채널 전체 조회 성공");

        // 해당 Map을 사용하여 채널 응답 Dto로 변환 및 응답
        return channels.stream()
            .map(channel -> channelMapper.toDto(
                channel, lastMessageMap.get(channel.getId())
            )).toList();
    }


    @Transactional
    @Override
    public void delete(UUID channelId) {
        // 채널 삭제 메서드 시작 로그
        log.trace("채널 삭제 메서드 시작: channelId={}", channelId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다!"));
        channelRepository.delete(channel);
        log.info("채널 삭제 성공: channelName={}", channel.getName());
    }
}
