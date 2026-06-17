package com.sprint.mission.discodeit.security.authorization;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// ReadStatus 권한 판단 클래스
@Component
@RequiredArgsConstructor
public class ReadStatusAuthorizationEvaluator {

    private final ReadStatusRepository readStatusRepository;

    // ReadStatus를 수정/삭제할 수 있는 권한인지 확인
    public boolean isOwner(UUID readStatusId, DiscodeitUserDetails principal) {
        if (readStatusId == null) {
            throw new InvalidInputException("readStatusId", null);
        }

        // 인증된 사용자 null 여부
        if (principal == null) {
            return false;
        }

        // 사용자 조회
        ReadStatus readStatus = readStatusRepository.findByIdWithUserAndChannel(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

        return readStatus.getUser().getId().equals(principal.getUserDto().id());
    }

}
