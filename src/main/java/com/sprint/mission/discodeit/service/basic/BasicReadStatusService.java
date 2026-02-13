package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateDto dto) {
        checkValidation(dto.userId(),dto.channelId());
        return readStatusRepository.save(new ReadStatus(dto.userId(),dto.channelId()));
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.find(readStatusId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id,ReadStatusUpdateDto dto) {
        ReadStatus status = find(id);
        status.update();//마지막 시간 현재로 업데이트
        return readStatusRepository.save(status);
    }

    @Override
    public void delete(UUID readStatusId) {
        find(readStatusId);
        readStatusRepository.delete(readStatusId);
    }

    private void checkValidation(UUID userId, UUID channelId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        if(channelRepository.findById(channelId).isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND);
        }
        if(readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.READ_STATUS_ALREADY_EXIST);
        }
    }
}
