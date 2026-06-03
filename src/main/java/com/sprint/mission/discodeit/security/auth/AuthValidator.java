package com.sprint.mission.discodeit.security.auth;

import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/*
    AuthValidator
    -------------
    메서드 요청자와 권한 소유자의 일치 여부를 검증하는 클래스
 */
@Component("authValidator")
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 요청자와 대상 사용자 일치 여부 검증
    @Transactional(readOnly = true)
    public boolean isSelf(UUID userId, String currentUsername) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return targetUser.getUsername().equals(currentUsername);
    }

    // 요청자와 메시지 작성자 일치 여부 검증
    @Transactional(readOnly = true)
    public boolean isMessageOwner(UUID messageId, String currentUsername) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        return targetMessage.getAuthor().getUsername().equals(currentUsername);
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 메시지 반환
    private MessageEntity getMessageEntityOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }
}
