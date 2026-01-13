package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MsgService;

import java.util.List;

public class MessageHelper {
    public static Message safeCreateMsg(MsgService service, String context, Channel channel, User user){
        try{
            return service.createMessage(context, channel, user);
        } catch(IllegalStateException | NullPointerException e){
            System.out.println(e);
            return null;
        }
    }

    public static List<Message> safeReadMsgbyChannel(MsgService service, Channel channel){
        try{
            return service.readMessageByChannel(channel);
        } catch(NullPointerException e){
            System.out.println(e);
            return null;
        }
    }

    public static List<Message> safeReadMsgbyUser(MsgService service, User user){
        try{
            return service.readMessageByAuthor(user);
        } catch(NullPointerException e){
            System.out.println(e);
            return null;
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

    public static Message safeDeleteMsg(MsgService service, Message message){
        try{
            return service.deleteMessage(message.getId());

        } catch(NullPointerException e){
            System.out.println("해당 메시지는 유효하지 않습니다.");
            return null;
        }
    }

}
