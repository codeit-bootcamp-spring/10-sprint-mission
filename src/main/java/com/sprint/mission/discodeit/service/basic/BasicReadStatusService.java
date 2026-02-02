package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.mapper.ReadStatusDTOMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusDTOMapper readStatusDTOMapper;


    @Override
    public ReadStatusResponseDTO create(ReadStatusCreateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");

        Channel channel = channelRepository.findById(req.channelId())
                .orElseThrow(
                        () -> new IllegalStateException("채널이 존재하지 않습니다.")
                );

        User user = userRepository.findById(req.userId())
                .orElseThrow(
                        () -> new IllegalStateException("유저가 존재하지 않습니다.")
                );

        if(readStatusRepository.findAll().stream().anyMatch(rs ->
            channel.getId().equals(rs.getChannelID()) && user.getId().equals(rs.getUserID()))
        ){
            throw new IllegalStateException("해당 ReadStatus 정보가 중복됩니다.");
        }

        ReadStatus readStatus = readStatusDTOMapper.createReqToReadStatus(req);
        ReadStatus saved = readStatusRepository.save(readStatus);
        return readStatusDTOMapper.rsToResponse(saved);
    }

    @Override
    public ReadStatusResponseDTO find(UUID rsId) {
        Objects.requireNonNull(rsId,"유효하지 않은 ReadStatus ID 입니다.");
        ReadStatus readStatus = readStatusRepository.findByID(rsId);
        return readStatusDTOMapper.rsToResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponseDTO> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId,"유효하지 않은 유저 ID 입니다.");

        return readStatusRepository.findAll().stream()
                .filter(rs -> userId.equals(rs.getUserID()))
                .map(readStatusDTOMapper::rsToResponse
                ).toList();

    }

    @Override
    public ReadStatusResponseDTO update(ReadStatusUpdateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

        ReadStatus readStatus = readStatusRepository
                .findAll()
                .stream()
                .filter(rs -> req.id().equals(rs.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 ReadStatus 입니다!"));

        readStatus.update(req.lastReadAt());
        ReadStatus saved = readStatusRepository.save(readStatus);

        return new ReadStatusResponseDTO(readStatus.getId(),
                req.channelId(),
                req.userId(),
                saved.getLastReadAt(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());

    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID입니다.");

        readStatusRepository.deleteByID(id);

    }
}
