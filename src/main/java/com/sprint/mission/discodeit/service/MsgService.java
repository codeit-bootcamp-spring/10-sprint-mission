package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MsgService {
    public Message createMessage(String context, Channel channel, User user); // 생성
   // public Message createMessage(String context, String channelName, String userName);
    public Message readMessage(UUID uuid);
    public Message updateMessage(UUID uuid, String context); // 업데이트
    public void deleteMessage(UUID uuid); // 삭제
    public ArrayList<Message> readAllMessage(); // 모두 조회

}
