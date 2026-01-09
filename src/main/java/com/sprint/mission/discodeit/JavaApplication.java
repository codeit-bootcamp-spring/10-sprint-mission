package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannel;
import com.sprint.mission.discodeit.service.jcf.JCFMessage;
import com.sprint.mission.discodeit.service.jcf.JCFUser;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        JCFUser userdata = JCFUser.getInstance();
        JCFChannel channeldata = JCFChannel.getInstance();
        JCFMessage messagedata = JCFMessage.getInstance();

        User user1 = new User("초시");
        User user2 = new User("초코");
        userdata.create(user1);
        userdata.create(user2);

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Message mes1 = new Message(user1, "이게 뭐지", ch1);
        Message mes2 = new Message(user2, "그게 먼데", ch1);

        ch1.addMessage(messagedata.create(mes1));
        ch1.addMessage(messagedata.create(mes2));

        //불러오기

        messagedata.read(mes1.getId());
        messagedata.readAll();

        channeldata.read(ch2.getId());
        channeldata.readAll();

        userdata.read(user2.getId());
        userdata.readAll();

        //수정하기&조회
        Message tmpmsg = new Message(mes1.getUser(), "이게 대체 뭐지?", mes1.getChannel());
        messagedata.update(mes1, tmpmsg);
        messagedata.read(mes1.getId());

        User tmpuser = new User("초코유");
        userdata.update(user2, tmpuser);
        messagedata.readAll();

        Channel tmpch = new Channel("코딩 하는 채널", ch1.getChannelDescription());
        channeldata.update(ch1, tmpch);
        channeldata.readAll();

        //삭제하기, 조회
        messagedata.delete(mes2);
        messagedata.readAll();

        userdata.delete(user2);
        userdata.readAll();

        channeldata.delete(ch2);
        channeldata.readAll();














    }
}
