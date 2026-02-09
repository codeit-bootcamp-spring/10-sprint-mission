package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// 사용자 관리

/*
학습 내용
 * HTTP 통신은 Request와 Response로 이루어진다.
 * HTTP Response의 구조
     * Status Line -> 상태 코드 (200, 404 ...)
     * Headers -> 메타데이터 (데이터 타입, 길이, 서버 정보 등)
     * Body -> 실제 전송 데이터
 */

@RestController
// @RestController =  @Controller + @ResponseBody
// 해당 클래스가 HTTP 요청을 처리하는 컨트롤러임을 나타내며, 모든 메서드의 반환 값을 HTTP 응답 본문(Response Body)에 직접 쓰겠다는 의미
// 스프링이 내부적으로 MappingJackson2HttpMessageConverter 등을 사용하여 자바 객체를 JSON 문자열로 자동 변환해 줌
@RequestMapping("/api/v1/users")
// 해당 컨트롤러 내의 모든 메서드에 공통으로 적용될 기본 경로를 정의
// 특정 URL 요청을 자바의 특정 메서드와 연결하는 역할
// 여러 속성을 통해 요청을 필터링할 수도 있음
// 클래스 레벨 매핑, 메서드 레벨 매핑
public class UserController {
    private final UserService userService;

    @Autowired // 생성자가 하나일 경우 @AutoWired 생략 가능
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자를 등록할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    // 해당 메서드를 특정 HTTP 경로와 메서드에 바인딩
    // 클라이언트가 POST 방식으로 요청을 보낼 때만 메서드 실행
    // @PostMapping을 최근에는 더 많이 사용
    // method를 생략할 경우 모든 HTTP 메서드에 응답하게 되어 보안 취약점이 될 수 있으므로 유의
    public ResponseEntity<UserResponse> signUp(@RequestBody UserCreateRequest request) {
        // ResponseEntity -> HTTP 전체 메시지를 구성할 수 있게 해주는 스프링의 클래스
            // HTTPEntity 클래스 상속 받음
            // 헤더 + 본문 + 상태코드
        // @RequestBody -> 클라이언트가 보내는 JSON 데이터를 자바 객체인 UserCreateRequest(DTO)로 변환하려면 필요
        UserResponse response = userService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
        // CREATED -> 201 : 요청이 성공적으로 처리되었으며, 자원이 생성되었음을 나타내는 성공 상태 응답 코드
    }
    // TODO: Validation 추가
    // TODO: Exception Handling

    // 사용자 정보를 수정할 수 있다.
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            // @PathVariable -> URL 경로에 포함된 동적인 값을 파라미터로 받아오는 도구
            // 특정 리소스를 수정하거나 조회할 때는 URL에 해당 리소스의 고유 식별자를 포함하는 것이 REST API의 표준 방식
            @RequestBody UserUpdateRequest request
    ) {
        // 서비스 계층에 수정을 위임
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
        // 200 : 요청이 성공했음을 나타내는 성공 응답 상태 코드
    }
    // TODO: 존재하지 않는 사용자 수정 시도 -> 404 반환
    // TODO: 현재 API는 id만 알면 누구나 다른 사람의 정보를 수정할 수 있는 구조 -> 보안 문제

    // 사용자를 삭제할 수 있다.
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        // 서비스 계층에 삭제 처리를 위임
        userService.delete(id);

        // 삭제 성공 시 관례적으로 '204 No Content'를 반환
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        // NO_CONTENT -> 204 : 요청이 성공했으나 응답 본문에 보낼 데이터는 없다
    }


}

