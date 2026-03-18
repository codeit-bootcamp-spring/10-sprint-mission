package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserStatusDTOMapper userStatusDTOMapper;

    @Override
    public UserStatus find(UUID id) {
        Objects.requireNonNull(id, "???レ챺???? ??? ID????낇돲??");
        return userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("?????UserStatus???釉뚰????? ?????????덊렡!"));
    }

    @Override
    @Transactional
    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req) {
        Objects.requireNonNull(userId, "???レ챺???? ??? userStatus ??筌뤿걩?????????덊렡!");
        Objects.requireNonNull(req, "???レ챺???? ??? ??釉먯뒜?????낇돲??");
        Optional<UserStatus> optUserStatus = userStatusRepository.findByUserId(userId);
        if (optUserStatus.isEmpty()) {
            throw new NoSuchElementException("?????UserStatus 癲ル슓??젆???????⑤챶苡?");
        }
        UserStatus userStatus = optUserStatus.get();
        userStatus.update(req.newLastActiveAt());

        return userStatusDTOMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "???レ챺???? ??? ID ????낇돲??");
        userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("?????UserStatus??癲ル슓??젆???????⑤챶苡?"));
        userStatusRepository.deleteById(id);
    }

}
