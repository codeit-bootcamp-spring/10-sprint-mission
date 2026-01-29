package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //기능 정의

    //User createUser(String userName, String alias, String email, String password);

    UserResponse createUser(UserCreateRequest userCreateRequest);
    // 유저 조회 -> id로 이름을..? -> 시스템 안에서만. -> 유저끼리는 이름으로 조회하기로..?
    UserResponse findUserById(UUID id);

    List<UserResponse> getUserAll();
    // 유저 전체 조회 -> 전체는 리스트로 받아야할듯. 반환 타입 리스트

    //User updateUser(UUID uuid, String newName, String newAlias);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);
    // 유저 정보 갱신 -> id로 받으면 수정할 멤버변수 못받음. -> 전체 객체로 받아야../

    void deleteUser(UUID uuid);
    // 유저 삭제... -> 그냥 id로 삭제 ? User 필요없을듯. 서로는 삭제.....?

    // 추가
    // 유저끼리 유저를 조회..
    List<UserResponse> getUserByName(String userName);

    User getUserByAlias(String alias);


}


