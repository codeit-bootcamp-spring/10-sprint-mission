package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageHelper {



    public static Message safeCreateMsg(MessageService service, String context, Channel channel, User user){
        try{
            return service.createMessage(context, channel.getId(), user.getUserId());
        } catch(IllegalStateException | NullPointerException e){
            System.out.println(e);
            return null;
        }
    }

    public static Optional<Message> safeReadMsg(MessageService service, Message message){
        try{
            System.out.println(service.readMessage(message.getId()));
            return Optional.ofNullable(service.readMessage(message.getId()));
        } catch(IllegalStateException | NullPointerException e){
            System.out.println("존재하지 않거나 유효하지 않은 메시지입니다.");
            return Optional.empty();
        }

    }

    public static Message safeUpdateMsg(MessageService service, Message message, String context){
        try{
            return service.updateMessage(message.getId(), context);
        } catch(NullPointerException e){
            System.out.println("해당 메시지는 유효하지 않습니다.");
            return null;
        }
    }

    public static void safeDeleteMsg(MessageService service, Message message){
        try{
            service.deleteMessage(message.getId());

        } catch(NullPointerException e){
            System.out.println("해당 메시지는 유효하지 않습니다.");

        }
    }

}
