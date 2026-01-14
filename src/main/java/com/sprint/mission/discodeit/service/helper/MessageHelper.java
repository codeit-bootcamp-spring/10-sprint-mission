package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MsgService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MessageHelper {
    public static Message safeCreateMsg(MsgService service, String context, Channel channel, User user){
        try{
            return service.createMessage(context, channel, user);
        } catch(IllegalStateException | NullPointerException e){
            System.out.println(e);
            return null;
        }
    }

    public static Optional<Message> safeReadMsg(MsgService service, Message message){
        try{
            System.out.println(service.readMessage(message.getId()));
            return Optional.ofNullable(service.readMessage(message.getId()));
        } catch(IllegalStateException | NullPointerException e){
            System.out.println("존재하지 않거나 유효하지 않은 메시지입니다.");
            return Optional.empty();
        }

    }

    public static Message safeUpdateMsg(MsgService service, Message message, String context){
        try{
            return service.updateMessage(message.getId(), context);
        } catch(NullPointerException e){
            System.out.println("해당 메시지는 유효하지 않습니다.");
            return null;
        }
    }

    public static void safeDeleteMsg(MsgService service, Message message){
        try{
            service.deleteMessage(message.getId());

        } catch(NullPointerException e){
            System.out.println("해당 메시지는 유효하지 않습니다.");

        }
    }

}
