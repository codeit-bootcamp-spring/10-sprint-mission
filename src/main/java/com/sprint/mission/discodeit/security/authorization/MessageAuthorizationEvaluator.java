package com.sprint.mission.discodeit.security.authorization;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// 메시지 권한 판단 클래스
@Component
@RequiredArgsConstructor
public class MessageAuthorizationEvaluator {

    private final MessageRepository messageRepository;

    // 메시지 작성자 여부 확인
    public boolean isAuthor(UUID messageId, DiscodeitUserDetails principal) {
        if (messageId == null) {
            throw new InvalidInputException("messageId", null);
        }

        // 인증된 사용자 null 여부
        if (principal == null) {
            return false;
        }

        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        return message.getAuthor().getId().equals(principal.getUserDto().id());
    }
}
