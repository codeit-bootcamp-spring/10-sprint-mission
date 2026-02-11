package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
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

    private final ReadStatusMapper mapper;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request){
        request.validate();
        // 관련 channel/user 없으면 제외
        userRepository.findById(request.userId())
                .orElseThrow(()->new NoSuchElementException("유저가 없습니다."));
        channelRepository.findById(request.channelId())
                .orElseThrow(()-> new NoSuchElementException("채널이 없습니다. "));

        //같은user/channel ID 있으면 예외?
        boolean exists = readStatusRepository.findAll().stream()
                .anyMatch(rs ->
                        rs.getUserId().equals(request.userId())&&
                        rs.getChannelId().equals(request.channelId())
                );
        if(exists) throw new IllegalArgumentException("이미 해당 유저/채널의 readstatus가 존재합니다.");
        ReadStatus rs = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(rs);
        return mapper.toResponse(rs);
    }

    @Override
    public ReadStatusResponse findById(UUID id) {
        if(id == null) throw new IllegalArgumentException("id는 null일수 업습니다.");
        ReadStatus rs = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus가 없습니다."));
        return mapper.toResponse(rs);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId){
        if(userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        return readStatusRepository.findAll().stream()
                .filter(rs -> userId.equals(rs.getUserId()))
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request){
        request.validate();

        ReadStatus rs = readStatusRepository.findById(request.readStatusId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus가 없습니다."));
        if(request.readNow()){
            rs.readNow();;
        }
        readStatusRepository.save(rs);
        return mapper.toResponse(rs);
    }
    @Override
    public void delete(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null알수 없습니다.");
        readStatusRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("삭제할 readStatus가 없습니다."));
        readStatusRepository.delete(id);
    }

}
