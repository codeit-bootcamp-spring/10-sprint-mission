package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

public class ChannelHelper {
    // try-catch 반복을 자제하기 위해 만들어진 호출을 감싸는 헬퍼 함수
    public static Channel safeCreateChannel(ChannelService service, String name, String content, Channel.CHANNEL_TYPE type){
        try{
            return service.createChannel(name, content, type);
        } catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public static void safeDeleteChannel(ChannelService service, Channel channel){
        try{
            service.deleteChannel(channel.getId());
        } catch (NullPointerException e){
            System.out.println("해당 채널이 null이므로, 식별자 id가 유효하지 않습니다.");

        } catch ( Exception e){
            System.out.println(e);

        }
    }

    public static Channel safeReadChannel(ChannelService service, Channel channel){
        try{
            return service.readChannel(channel.getId());

        } catch (NullPointerException e){
            System.out.println("해당 채널이 null이므로, 식별자 id가 유효하지 않습니다.");
            return null;
        } catch ( Exception e){
            System.out.println(e);
            return null;
        }
    }

    public static Channel safeUpdateChannel(ChannelService service, Channel channel, String name, String content, Channel.CHANNEL_TYPE type) {
        try {

            return service.updateChannel(channel.getId(), name, content, type);
        } catch (NullPointerException e){
            System.out.println("해당 채널이 null이므로, 식별자 id가 유효하지 않습니다.");
            return null;
        } catch ( Exception e){
            System.out.println(e);
            return null;
        }
    }

}
