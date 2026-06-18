package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.UserOnlineStatusChangedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserOnlineStatusChangedEventListener {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @EventListener
  public void handle(UserOnlineStatusChangedEvent event) {
    userRepository.findWithDetailsById(event.userId())
        .map(user -> userMapper.toDto(user, jwtRegistry))
        .ifPresent(userDto ->
            eventPublisher.publishEvent(new UserUpdatedEvent(userDto))
        );
  }
}
