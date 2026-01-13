package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.Objects;

public class UserHelper {
    public static User safeCreateUser(UserService service, String name, String email, String userID){
        try{
            return service.createUser(name,email,userID);
        } catch(IllegalStateException e){
            System.out.println(e);
            return null;
        }
    }

    public static User safeDeleteUser(UserService service, User user){

        try{
            return service.deleteUser(user.getId());
        } catch(NullPointerException e){
            System.out.println("해당 유저는 null이므로, 유효하지 않습니다.");
            return null;
        }
    }

    public static User safeUpdateUser(UserService service, User user, String userId, String userName, String email){
        try{
            return service.updateUser(user.getId(), userId, userName, email );
        } catch(NullPointerException | IllegalStateException | IllegalAccessError e){
            System.out.println(e);
            return null;
        }
    }

    public static User safeReadUser(UserService service, User user){

        try{
            return service.readUser(user.getId());
        } catch (NullPointerException e){
            System.out.println("해당 유저는 null입니다.");
            return null;
        }
    }
}
