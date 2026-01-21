package com.sprint.mission;

import com.sprint.mission.entity.User;
import com.sprint.mission.repository.file.FileUserRepository;
import com.sprint.mission.service.UserService;
import com.sprint.mission.service.basic.BasicUserService;
import com.sprint.mission.service.jcf.JCFServiceFactory;
import com.sprint.mission.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void testUserService(UserService service) {
        User user1 = service.create("jonas");
        System.out.printf("user id: %s, name: %s%n", user1.getId(), user1.getUserName());

        if (user1.equals(service.get(user1.getId()))) {
            System.out.println("same user");
        }
        User user2 = service.create("lee");
        if (!user1.equals(user2)) {
            System.out.println("different user");
        }
        UUID userid1 = user1.getId();
        service.update(userid1, "jonas in dark");
        System.out.printf("user id: %s, name: %s%n", user1.getId(), user1.getUserName());

        service.delete(userid1);
        System.out.println("delete user1");
        try {
            service.get(userid1);
        } catch (IllegalArgumentException error) {
            System.out.println(error.getMessage());
        }
    }

    public static void main(String[] args) {
        testUserService(JCFServiceFactory.getUserService());
        System.out.println("#####################");
        testUserService(new BasicUserService(new FileUserRepository()));
    }
}