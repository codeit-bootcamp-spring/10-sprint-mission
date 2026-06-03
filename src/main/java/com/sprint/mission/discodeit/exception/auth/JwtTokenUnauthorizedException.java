package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    JwtTokenUnauthorizedException
    -----------------------------
     JWT Token이 유효하지 않을 경우, 발생하는 예외 클래스
 */
public class JwtTokenUnauthorizedException extends AuthException {

    public JwtTokenUnauthorizedException() {
        super(ErrorCode.JWT_TOKEN_UNAUTHORIZED);
    }
}
