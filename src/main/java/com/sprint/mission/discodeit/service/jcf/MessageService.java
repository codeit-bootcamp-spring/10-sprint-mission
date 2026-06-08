package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  Message createMessage(String context, UUID channelID, String userID); // 생성

  // public Message createMessage(String context, String channelName, String userName);
  Message readMessage(UUID uuid);

  List<Message> readMessagebyUser(String userID);

  List<Message> readMessagebyChannel(UUID channelID);

  Message updateMessage(UUID uuid, String context); // 업데이트

  void deleteMessage(UUID uuid); // 삭제

  List<Message> readAllMessage(); // 모두 조회

  void save(Message message);

  void setUserService(UserService userService);

  void setChannelService(ChannelService channelService);
}
