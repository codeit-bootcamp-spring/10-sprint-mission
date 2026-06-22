package com.sprint.mission.discodeit.support.mapper;

import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapper;
import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapperImpl;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentApiMapper;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentApiMapperImpl;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelApiMapper;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelApiMapperImpl;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageApiMapper;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageApiMapperImpl;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationApiMapper;
import com.sprint.mission.discodeit.notification.presentation.mapper.NotificationApiMapperImpl;
import com.sprint.mission.discodeit.readstatus.presentation.mapper.ReadStatusApiMapper;
import com.sprint.mission.discodeit.readstatus.presentation.mapper.ReadStatusApiMapperImpl;
import com.sprint.mission.discodeit.user.presentation.mapper.UserApiMapper;
import com.sprint.mission.discodeit.user.presentation.mapper.UserApiMapperImpl;
import java.util.HashMap;
import java.util.Map;

public final class ApiMapperContainer {

  private static final Map<Class<?>, Object> container = new HashMap<>();

  static {
    BinaryContentApiMapper binaryContentApiMapper = new BinaryContentApiMapperImpl();
    container.put(BinaryContentApiMapper.class, binaryContentApiMapper);
    UserApiMapper userApiMapper = new UserApiMapperImpl(binaryContentApiMapper);
    container.put(UserApiMapper.class, userApiMapper);
    ChannelApiMapper channelApiMapper = new ChannelApiMapperImpl(userApiMapper);
    container.put(ChannelApiMapper.class, channelApiMapper);
    MessageApiMapper messageApiMapper = new MessageApiMapperImpl(userApiMapper,
        binaryContentApiMapper);
    container.put(MessageApiMapper.class, messageApiMapper);
    ReadStatusApiMapper readStatusApiMapper = new ReadStatusApiMapperImpl();
    container.put(ReadStatusApiMapper.class, readStatusApiMapper);
    AuthMapper authMapper = new AuthMapperImpl();
    container.put(AuthMapper.class, authMapper);
    NotificationApiMapper notificationApiMapper = new NotificationApiMapperImpl();
    container.put(NotificationApiMapper.class, notificationApiMapper);
  }

  private ApiMapperContainer() {
  }

  public static <T> T get(Class<T> clazz) {
    return clazz.cast(container.get(clazz));
  }
}
