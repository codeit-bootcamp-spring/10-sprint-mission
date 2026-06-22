package com.sprint.mission.discodeit.support.mapper;

import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapper;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentApiMapper;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelApiMapper;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageApiMapper;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationApiMapper;
import com.sprint.mission.discodeit.readstatus.presentation.mapper.ReadStatusApiMapper;
import com.sprint.mission.discodeit.user.presentation.mapper.UserApiMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GlobalApiMapperTestConfig {

  @Bean
  public BinaryContentApiMapper binaryContentApiMapper() {
    return ApiMapperContainer.get(BinaryContentApiMapper.class);
  }

  @Bean
  public UserApiMapper userApiMapper() {
    return ApiMapperContainer.get(UserApiMapper.class);
  }

  @Bean
  public ChannelApiMapper channelApiMapper() {
    return ApiMapperContainer.get(ChannelApiMapper.class);
  }

  @Bean
  public MessageApiMapper messageApiMapper() {
    return ApiMapperContainer.get(MessageApiMapper.class);
  }

  @Bean
  public ReadStatusApiMapper readStatusApiMapper() {
    return ApiMapperContainer.get(ReadStatusApiMapper.class);
  }

  @Bean
  public AuthMapper authMapper() {
    return ApiMapperContainer.get(AuthMapper.class);
  }

  @Bean
  public NotificationApiMapper notificationApiMapper() {
    return ApiMapperContainer.get(NotificationApiMapper.class);
  }
}
