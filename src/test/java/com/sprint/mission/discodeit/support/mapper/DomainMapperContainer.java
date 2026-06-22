package com.sprint.mission.discodeit.support.mapper;

import com.sprint.mission.discodeit.binarycontent.application.mapper.BinaryContentDomainMapper;
import com.sprint.mission.discodeit.binarycontent.application.mapper.BinaryContentDomainMapperImpl;
import com.sprint.mission.discodeit.channel.application.mapper.ChannelDomainMapper;
import com.sprint.mission.discodeit.channel.application.mapper.ChannelDomainMapperImpl;
import com.sprint.mission.discodeit.channel.application.mapper.ChannelPayloadMapper;
import com.sprint.mission.discodeit.channel.application.mapper.ChannelPayloadMapperImpl;
import com.sprint.mission.discodeit.message.application.mapper.MessageDomainMapper;
import com.sprint.mission.discodeit.message.application.mapper.MessageDomainMapperImpl;
import com.sprint.mission.discodeit.message.application.mapper.MessagePayloadMapper;
import com.sprint.mission.discodeit.message.application.mapper.MessagePayloadMapperImpl;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationDomainMapper;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationDomainMapperImpl;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationPayloadMapper;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationPayloadMapperImpl;
import com.sprint.mission.discodeit.readstatus.application.mapper.ReadStatusDomainMapper;
import com.sprint.mission.discodeit.readstatus.application.mapper.ReadStatusDomainMapperImpl;
import com.sprint.mission.discodeit.user.application.mapper.UserPayloadMapper;
import com.sprint.mission.discodeit.user.application.mapper.UserPayloadMapperImpl;
import java.util.HashMap;
import java.util.Map;

public final class DomainMapperContainer {

  private static final Map<Class<?>, Object> container = new HashMap<>();

  static {
    BinaryContentDomainMapper binaryContentDomainMapper = new BinaryContentDomainMapperImpl();
    container.put(BinaryContentDomainMapper.class, binaryContentDomainMapper);
    ChannelDomainMapper channelDomainMapper = new ChannelDomainMapperImpl();
    container.put(ChannelDomainMapper.class, channelDomainMapper);
    ChannelPayloadMapper channelPayloadMapper = new ChannelPayloadMapperImpl();
    container.put(ChannelPayloadMapper.class, channelPayloadMapper);
    MessageDomainMapper messageDomainMapper = new MessageDomainMapperImpl();
    container.put(MessageDomainMapper.class, messageDomainMapper);
    MessagePayloadMapper messagePayloadMapper = new MessagePayloadMapperImpl();
    container.put(MessagePayloadMapper.class, messagePayloadMapper);
    ReadStatusDomainMapper readStatusDomainMapper = new ReadStatusDomainMapperImpl();
    container.put(ReadStatusDomainMapper.class, readStatusDomainMapper);
    NotificationDomainMapper notificationDomainMapper = new NotificationDomainMapperImpl();
    container.put(NotificationDomainMapper.class, notificationDomainMapper);
    NotificationPayloadMapper notificationPayloadMapper = new NotificationPayloadMapperImpl();
    container.put(NotificationPayloadMapper.class, notificationPayloadMapper);
    UserPayloadMapper userPayloadMapper = new UserPayloadMapperImpl();
    container.put(UserPayloadMapper.class, userPayloadMapper);
  }

  private DomainMapperContainer() {
  }

  public static <T> T get(Class<T> clazz) {
    return clazz.cast(container.get(clazz));
  }
}
