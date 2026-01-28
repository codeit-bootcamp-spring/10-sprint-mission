package com.sprint.mission.discodeit.service.helper;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.UserService;

public class UserHelper {
    public static User safeCreateUser(UserService service, String name, String email, String userID){
        try{
            return service.createUser(name,email,userID);
        } catch(IllegalStateException e){
            System.out.println(e);
            return null;
        }
    }

    public static void safeDeleteUser(UserService service, User user){

        try{
            service.deleteUser(user.getUserId());
        } catch(NullPointerException e){
            System.out.println("해당 유저는 null이므로, 유효하지 않습니다.");

        }
    }

    public static User safeUpdateUser(UserService service, String userId, String userName, String email){
        try{
            return service.updateUser(userId, userName, email );
        } catch(NullPointerException | IllegalStateException | IllegalAccessError e){
            System.out.println(e);
            return null;
        }
    }

    public static User safeReadUser(UserService service, User user){

        try{
            return service.readUser(user.getUserId());
        } catch (NullPointerException e){
            System.out.println("해당 유저는 null입니다.");
            return null;
        }
    }

    public static void safeJoinChannel(UserService userService, User user, Channel channel){
        try{
            userService.joinChannel(user.getUserId(), channel.getId());
        } catch(NullPointerException | IllegalStateException e){
            System.out.println(e);
        }
    }

    public static void safeExitChannel(UserService userService, User user, Channel channel){
        try{
            userService.exitChannel(user.getUserId(), channel.getId());
        }catch(NullPointerException | IllegalStateException e){
            System.out.println(e);
        }
    }
}
