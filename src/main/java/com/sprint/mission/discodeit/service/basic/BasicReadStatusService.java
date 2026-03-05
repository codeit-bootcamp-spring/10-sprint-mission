package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto createReadStatus(CreateReadStatusRequestDTO dto) {
        // 객체 검증
        User user = findUserOrThrow(dto.userId());
        Channel channel = findChannelOrThrow(dto.channelId());

        // 중복 검증
        checkStatusAlreadyExists(dto.userId(), dto.channelId());

        ReadStatus status = new ReadStatus(user, channel);
        readStatusRepository.save(status);

        return readStatusMapper.toDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDto findById(UUID statusId) {
        ReadStatus status = findReadStatusOrThrow(statusId);

        return readStatusMapper.toDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);
        List<ReadStatus> statuses = readStatusRepository.findAllByUser_Id(userId);

        return readStatusMapper.toDtoList(statuses);
    }

    @Override
    public ReadStatusDto updateReadStatus(UUID statusId, UpdateReadStatusRequestDTO dto) {
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        if (dto.newLastReadAt() == null) {
            throw new IllegalArgumentException("lastReadAt은 null값일 수 없습니다.");
        }

        ReadStatus status = findReadStatusOrThrow(statusId);
        status.updateLastReadAt(dto.newLastReadAt());

        return readStatusMapper.toDto(status);
    }

    @Override
    public void deleteById(UUID statusId) {
        findReadStatusOrThrow(statusId);

        readStatusRepository.deleteById(statusId);
    }

    private ReadStatus findReadStatusOrThrow(UUID statusId) {
        Objects.requireNonNull(statusId, "readStatusId는 null값일 수 없습니다.");

        return readStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 readStatus가 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 사용자가 존재하지 않습니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null값일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 채널이 존재하지 않습니다."));
    }

    private void checkStatusAlreadyExists(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUser_IdAndChannel_Id(userId, channelId)) {
            throw new IllegalArgumentException("이미 ReadStatus가 존재합니다.");
        }
    }
}
