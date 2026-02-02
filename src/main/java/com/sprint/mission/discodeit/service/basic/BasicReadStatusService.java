package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusDto.Response create(ReadStatusDto.CreateRequest request) {
        if (!userRepository.existsById(request.userId())) throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        if (!channelRepository.existsById(request.channelId())) throw new IllegalArgumentException("채널이 존재하지 않습니다.");

        boolean isDuplicate = readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(request.userId()) && rs.getChannelId().equals(request.channelId()));
        if (isDuplicate) throw new IllegalArgumentException("이미 존재하는 읽음 상태입니다.");

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );

        return convertToResponse(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusDto.Response findById(UUID id) {
        return convertToResponse(findReadStatusEntityById(id));
    }

    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ReadStatusDto.Response update(ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = findReadStatusEntityById(request.id());
        readStatus.update(request.lastReadAt());
        return convertToResponse(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID id) {
        findReadStatusEntityById(id);
        readStatusRepository.deleteById(id);
    }

    // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
    private ReadStatus findReadStatusEntityById(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("읽음 상태를 찾을 수 없습니다."));
    }

    // [헬퍼 메서드]: 엔티티를 클라이언트 응답용 DTO로 변환 및 데이터 가공
    private ReadStatusDto.Response convertToResponse(ReadStatus readStatus) {
        return new ReadStatusDto.Response(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
