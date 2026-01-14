package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    //생성
    Message create(String content, User user, Channel channel);

    //읽기
    Message findById(UUID id);

    //모두 읽기
    List<Message> findAll();

    //수정
    Message update(UUID id, String content);

    //삭제
    Message delete(UUID id);

}
