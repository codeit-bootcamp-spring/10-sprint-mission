package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;

// TODO: 기존 방식(Class + Lombk)과 레코드 방식(Record) 비교 후 정리 기록 남기기

/*
record가 자동으로 해주는 것들
- 모든 필드는 private final - 생성 후에는 값을 바꿀 수 없음(불변성 보장)
- 생성자 - 모든 필드를 받는 생성자를 자동으로 생성
- Getter 메서드 (주의! 이름 앞에 get이 붙지 않음 - username())
- equals(), hashCode(), toString() 자동 구현

// TODO: DTO 개념 관련 정리 필요
(정리 필요)
- 유효성 검증 로직을 DTO 클래스로 빼내어 핸들러 메서드의 간결함을 유지할 수 있다.
- 비용이 많이 드는 작업인 HTTP 요청의 수를 줄이기 위함
 */

// 회원가입 시 필요한 정보
public record UserCreateRequest (
        String username,
        String email,
        String password,
        BinaryContentRequest profileImage  // BinaryContentRequest DTO를 필드로 받음 (Null 가능)
        // TODO: 프로필 사진을 강제할 것인지 고민 필요 -> 기본 프로필 설정 or 프로필 강제 설정 -> 결정 필요
) {}