package com.sprint.mission.discodeit.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceAuthorizationService {

	private final MessageRepository messageRepository;

	public boolean canModifyUser(Authentication authentication, UUID targetUserId) {
		Optional<UUID> currentUserId = getCurrentUserId(authentication);
		return currentUserId.filter(uuid -> isSameUser(uuid, targetUserId)).isPresent();
	}

	@Transactional(readOnly = true)
	public boolean canModifyMessage(Authentication authentication, UUID messageId) {
		Optional<UUID> currentUserId = getCurrentUserId(authentication);
		if (currentUserId.isEmpty()) {
			return false;
		}

		Optional<UUID> messageAuthorId = messageRepository.findAuthorIdByMessageId(messageId);
		return messageAuthorId.map(uuid -> isSameUser(currentUserId.get(), uuid))
			.orElseGet(() -> isMessageMissing(messageId));
	}

	private Optional<UUID> getCurrentUserId(Authentication authentication) {
		if (authentication == null) {
			return Optional.empty();
		}
		if (!authentication.isAuthenticated()) {
			return Optional.empty();
		}
		if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
			return Optional.empty();
		}

		return Optional.ofNullable(userDetails.getUserDto().id());
	}

	private boolean isMessageMissing(UUID messageId) {
		return !messageRepository.existsById(messageId);
	}

	private boolean isSameUser(UUID currentUserId, UUID targetUserId) {
		return currentUserId != null && currentUserId.equals(targetUserId);
	}
}

