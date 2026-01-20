package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(String context, UUID channelID, String userID); // 생성
   // public Message createMessage(String context, String channelName, String userName);
    public Message readMessage(UUID uuid);
    public List<Message> readMessagebyUser(String userID);
    public List<Message> readMessagebyChannel(UUID channelID);
    public Message updateMessage(UUID uuid, String context); // 업데이트
    public void deleteMessage(UUID uuid); // 삭제
    public ArrayList<Message> readAllMessage(); // 모두 조회
}
