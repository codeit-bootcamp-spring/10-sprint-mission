package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface MsgService {
    public Message createMessage(String context, Channel channel, User user); // 생성
    public List<Message> readMessageByChannel(Channel channel); // 채널 ID를 통해 조회
    public List<Message> readMessageByAuthor(User user); // 작성자를 통해 조회
    public boolean updateMessage(UUID msgID, String context); // 업데이트
    public boolean deleteMessage(UUID msgID); // 삭제
    public ArrayList<Message> readAllMessage(); // 모두 조회

}
