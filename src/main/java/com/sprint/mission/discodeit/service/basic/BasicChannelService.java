package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.mapper.ChannelDTOMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelDTOMapper channelDTOMapper;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // 최신 메시지�?받아?�??Message�?리턴?�는 메소??
    private Message getLatestMessage(UUID channelId) {
        return messageRepository.findByChannelId(channelId)
            .stream()
            .max(Comparator.comparing(Message::getUpdatedAt))
            .orElse(null);
    }

    private Instant getLastMessageAt(Message last) {
        return last == null ? null : last.getUpdatedAt();
    }

    // Public 채널??만드??메소??
    // ?�청 DTO?�서 ?�드�?뽑아?� 채널 ?�성
    // Public??Private 처럼 ?��? 목록??받아?�하?��???
    @Override
    public ChannelResponseDTO createPublicChannel(PublicChannelCreateDTO req) {
        Objects.requireNonNull(req.name(), "?�효?��? ?��? 채널 ?�름?�니??");
        Objects.requireNonNull(req.description(), "?�효?��? ?��? 채널 ?�명?�니??");

        // 채널 리스?�에 중복?�는 ?�름???�으�??�외 ?�짐.
        if (channelRepository.findAll()
            .stream()
            .anyMatch(c -> req.name().equals(c.getName()))) {
            throw new IllegalStateException("?�당 채널???�름??중복?�니??");
        }

        // DTO 매퍼�??�용?�여 DTO -> entity ?�작
        // ?�성?�로 만들?�진 ?�스?�스�?채널 ?�포지?�리?�서 ?�속??
        Channel channel = channelDTOMapper.publicReqToChannel(req);
        Channel saved = channelRepository.save(channel);

        // 최종?�으�?만들?�진 채널???�용?�여 채널 ?�답 DTO�?리턴
        // ?�로 만들?�진 채널 -> 메시지 존재?��? ?�음 -> 메시지 ?�간 ?�보 null 처리
        return channelDTOMapper.channelToResponseDTO(saved, null);
    }


    // Private 채널??만드??메소??
    // ?�름�??�명?� ?�략 -> null 처리?
    @Override
    public ChannelResponseDTO createPrivateChannel(PrivateChannelCreateDTO req) {
        Objects.requireNonNull(req, "Invalid request");
        Objects.requireNonNull(req.users(), "User list is required");
        if (req.users().isEmpty()) {
            throw new IllegalStateException("User list is empty");
        }

        // ?�름�??�명 ?�략 -> null 처리 -> ?�속??
        Channel channel = channelDTOMapper.privateReqToChannel(req);
        Channel saved = channelRepository.save(channel);

        // ?�청 DTO???��? 목록???�트�?
        req.users().forEach(u -> {
            ReadStatus rs = new ReadStatus(u, channel.getId()); // �??��?�?readStatus ?�성.
            readStatusRepository.save(rs); // userStatus ?�속??
        });

        // 최종?�으�?만들?�진 channel???�용?�여 채널 ?�답 DTO�?리턴
        // ?�로 만든 채널 -> 메시지가 ?�음 -> 메시지 ?�간 ?�보?�을 null 처리
        return channelDTOMapper.channelToResponseDTO(saved, null);
    }


    // 채널 ID를 매개변수로 받고, ChannelViewDTO 만들어서
    // ChannelViewDTO가 최근 메세지 보임 (createdAt, updatedAT)
    @Override
    public ChannelResponseDTO find(UUID channelId) {

        // 채널 ID가 존재?�는지 검�?
        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(() -> new IllegalStateException("?�당 채널 ID가 존재?��? ?�습?�다."));

        // 메세지 리포지?�리?�서 channelID�??�해 메세지 리스?��? 뽑고 stream ->
        // max�??�용?�여 가????최근) createdAt??가�?메시지�?뽑고
        // 메시지가 존재?��? ?�으�?null 리턴? <- 추후 바꿔?�할??

        Message last = getLatestMessage(channelId);
        Instant lastMessageAt = last == null ? null : last.getUpdatedAt();

        // 채널 ?�?�이 Public?�라�?..
        return channelDTOMapper.channelToResponseDTO(channel, lastMessageAt);

    }


    // UserID�??�해 ?�정 ?��?가 �????�는 Channel 목록??조회?�도�??�는 메소??
    // Public 채널?� 모든 ?��??�게 공개 가??
    // Private 채널?� ?�당 ?��?가 가?�되???�을?�만 공개 가??
    @Override
    public List<ChannelResponseDTO> findAllByUserId(UUID userID) {
        Objects.requireNonNull(userID, "?�효?��? ?��? ?��?ID ?�니??");
        if (userRepository.findAll().stream().noneMatch(u -> userID.equals(u.getId()))) {
            throw new NoSuchElementException("?�당 ?��?가 존재?��? ?�습?�다!");
        }

        // 채널 ?�포지?�리?�서 모든 채널?�을 ?�트�?
        return channelRepository.findAll().stream()
            // filter�?채널 ?�?�이 Public?�거??채널 ?�?�이 Private?�면??채널???��? 리스?�에 userID가 ?�는 채널?�을 뽑아??
            .filter(c -> c.getType() == ChannelType.PUBLIC
                || (c.getType() == ChannelType.PRIVATE && c.getUserList().contains(userID)))

            .map(
                c -> {
                    // 메시지가 ?�으�??�간 ?�보?��? ??null 처리,
                    // 채널 ?�?�이 Private?�면 ?��? ?�보�?DTO??주입, Public?�면 �?리스?��? DTO??주입
                    return channelDTOMapper.channelToResponseDTO(c,
                        getLastMessageAt(getLatestMessage(c.getId())));
                }
            ).toList(); // 리스?�화 ?�여 반환??
    }


    // 채널???��?�?가?�시?�는 메소??
    @Override
    public ChannelResponseDTO join(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.userJoin(userId);
        channelRepository.save(channel);
        Message latestMessage = getLatestMessage(channelId);

        return channelDTOMapper.channelToResponseDTO(channel,
            getLastMessageAt(getLatestMessage(channelId)));
    }

    @Override
    public ChannelResponseDTO leave(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.userLeave(userId);
        Channel saved = channelRepository.save(channel);

        return channelDTOMapper.channelToResponseDTO(saved,
            getLastMessageAt(getLatestMessage(saved.getId())));
    }

    // ?�데?�트 ?�청 DTO�?매개변?�로 받아 채널???�데?�트?�는 ?�비??메소??
    @Override
    public ChannelResponseDTO update(UUID channelId, PublicChannelUpdateRequestDTO req) {
        Objects.requireNonNull(req, "?�효?��? ?��? ?�청?�니??");
        Objects.requireNonNull(channelId, "?�효?��? ?��? ?�별?�입?�다.");

        // 채널 ?�포지?�리?�서 req??채널ID�??�하??채널 찾음.
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalStateException("?�당 채널 존재?��? ?�음."));

        // Private 채널?� ?�정??불�??��?�??�외 ?�짐.
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("개인 채널?� ?�데?�트가 불�??�합?�다.");
        }

        // DTO ?��???값들???�용?�여 채널 ?�데?�트
        channel.update(req.newName(), req.newDescription());

        // ?�데?�트??채널???�속??
        Channel saved = channelRepository.save(channel);

        // ?�속?�된 채널 객체�??�답 DTO�?변??�?리턴
        return channelDTOMapper.channelToResponseDTO(saved,
            getLastMessageAt(getLatestMessage(channelId)));
    }

    // 채널 ID�?매개변?�로 받아 ?�당 채널????��?�는 메소??
    @Override
    public void delete(UUID channelId) {
        Objects.requireNonNull(channelId, "?�효?��? ?��? 채널ID ?�니??");

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        // ?�당 채널??존재?�는 메시지??ID�??�용
        // 메시지 ?�포지?�리??DeleteById�??�해 ??��?�다.
        messageRepository.findByChannelId(channelId)
            .forEach(m -> messageRepository.deleteById(m.getId()));

        // readStatus ?�포지?�리??모든 객체중에??
        // channelId�?가리키??객체�?filter�?뽑아?�고
        // forEach?� deleteByID 메소?��? ?�해 각각 ?�거.
        readStatusRepository.findAll()
            .stream()
            .filter(r -> channelId.equals(r.getChannelID()))
            .forEach(r -> readStatusRepository.deleteByID(r.getId()));

        // 목표 채널????�� -> ?�속?�는 ?�당 메소???��??�서 진행.
        channelRepository.deleteById(channelId);
    }
}
