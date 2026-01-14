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
        JCFUserService jcfU1 = new JCFUserService();
        UUID userId1 = jcfU1.create("홍길동").getId();
        JCFUserService jcfU2 = new JCFUserService();

        // 조회(단건)
        System.out.println(jcfU1.read(userId1));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfU1.readAll());
        System.out.println(jcfU2.readAll());

        // 수정
        jcfU1.update(userId1, "일이삼");
        jcfU1.update(UUID.randomUUID(), "아무거나 유저명");

        // 수정된 데이터 조회
        System.out.println(jcfU1.read(userId1));

        // 삭제
        jcfU1.delete(userId1);
        jcfU1.delete(UUID.randomUUID());

        // 조회를 통해 삭제되었는지 확인
        jcfU1.read(userId1);


        System.out.println();


        // Channel
        // 등록
        JCFChannelService jcfC1 = new JCFChannelService();
        UUID channelId1 = jcfC1.create("실험실").getId();
        JCFChannelService jcfC2 = new JCFChannelService();

        // 조회(단건)
        System.out.println(jcfC1.read(channelId1));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfC1.readAll());
        System.out.println(jcfC2.readAll());

        // 수정
        jcfC1.updateChannelname(channelId1, "변경된 채널명");
        jcfC1.updateChannelname(UUID.randomUUID(), "아무거나 채널명");

        // 수정된 데이터 조회
        System.out.println(jcfC1.read(channelId1));

        // 삭제
        jcfC1.delete(channelId1);
        jcfC1.delete(UUID.randomUUID());

        // 조회를 통해 삭제되었는지 확인
        jcfC2.read(channelId1);


        System.out.println();


        // Message
        // 등록
        JCFMessageService jcfM1 = new JCFMessageService();
        UUID messageId1 = jcfM1.create("테스트").getId();
        JCFMessageService jcfM2 = new JCFMessageService();

        // 조회(단건)
        System.out.println(jcfM1.read(messageId1));
        System.out.println(jcfU2.read(UUID.randomUUID()));

        // 조회(다건)
        System.out.println(jcfM1.readAll());
        System.out.println(jcfM2.readAll());

        // 수정
        jcfM1.update(messageId1, "메시지 변경");
        jcfM1.update(UUID.randomUUID(), "아무거나 메시지");

        // 수정된 데이터 조회
        System.out.println(jcfM1.read(messageId1));

        // 삭제
        jcfM1.delete(messageId1);
        jcfM1.delete(UUID.randomUUID());

        // 조회를 통해 삭제되었는지 확인
        jcfM1.read(messageId1);

    }
}