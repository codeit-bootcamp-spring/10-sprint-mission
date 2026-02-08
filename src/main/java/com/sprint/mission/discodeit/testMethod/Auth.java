package com.sprint.mission.discodeit.testMethod;

import com.sprint.mission.discodeit.service.AutoService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Auth {

    private final UserService userService;
    private final AutoService autoService;

    // 유저 로그인
    public void login(String userName, String password) {
        autoService.login(userName, password);
        System.out.println(userName + "유저 로그인에 성공했습니다.");
    }

    // 유저 로그아웃
    public void logout(String userName){
        autoService.logout(userName);
        System.out.println(userName + "유저가 로그아웃 되었습니다.");
    }
}
