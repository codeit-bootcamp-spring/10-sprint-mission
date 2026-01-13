package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        // User
        // 등록
        User user1 = new User();
        JCFUserService jcfU1 = new JCFUserService(user1);
        JCFUserService jcfU2 = new JCFUserService();

        // 조회(단건)
        System.out.println(jcfU1.read(user1.getId()));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfU1.readAll());
        System.out.println(jcfU2.readAll());

        // 수정
        User userUpdateTest = new User();
        jcfU1.update(userUpdateTest);

        // 수정된 데이터 조회
        System.out.println(jcfU1.read(userUpdateTest.getId()));

        // 삭제
        User userDeleteTest = jcfU1.delete(user1.getId());

        // 조회를 통해 삭제되었는지 확인
        jcfU1.read(user1.getId());



        // Channel
        // 등록
        Channel channel1 = new Channel();
        JCFChannelService jcfC1 = new JCFChannelService(channel1);
        JCFChannelService jcfC2 = new JCFChannelService();

        // 조회(단건)
        System.out.println(jcfC1.read(channel1.getId()));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfC1.readAll());
        System.out.println(jcfC2.readAll());

        // 수정
        Channel channelUpdateTest = new Channel();
        jcfC1.update(channelUpdateTest);

        // 수정된 데이터 조회
        System.out.println(jcfC1.read(channelUpdateTest.getId()));

        // 삭제
        Channel channelDeleteTest = jcfC1.delete(channel1.getId());

        // 조회를 통해 삭제되었는지 확인
        jcfC2.read(channel1.getId());



        // Message
        // 등록
        Message message1 = new Message();
        JCFMessageService jcfM1 = new JCFMessageService(message1);
        JCFMessageService jcfM2 = new JCFMessageService();

        // 조회(단건)
        System.out.println(jcfM1.read(message1.getId()));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfM1.readAll());
        System.out.println(jcfM2.readAll());

        // 수정
        Message messageUpdateTest = new Message();
        jcfM1.update(messageUpdateTest);

        // 수정된 데이터 조회
        System.out.println(jcfM1.read(messageUpdateTest.getId()));

        // 삭제
        Message messageDeleteTest = jcfM1.delete(message1.getId());

        // 조회를 통해 삭제되었는지 확인
        jcfM1.read(message1.getId());

    }
}