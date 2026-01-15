package com.sprint.mission.discodeit.entity;

// 채널별 역할/권한(Role)의 종류를 정의합니다.
// 사용의 편의성을 위해 따로 Enum 파일을 분리
// [] 권한에 따른 추방 등 기능 구현 시 enum 수정 (추후 구현)

public enum ChannelRole {
    OWNER,         // 서버 소유자(Channel[Server] Owner)
    ADMIN, // 관리자(Administrator) - Owner랑 동등한 권한을 가지나 Owner를 추방하거나 차단할 수 없음
//  ROLE,          // 역할(Role) 보유자(중간 관리자 및 멤버)
        // 서버 관리자가 직접 만든 커스텀 역할을 부여받은 사용자
        // 위계 시스템이 존재하여 상위 역할을 가진 사람은 자신보다 하위 역할을 가진 사람만 관리(추방, 차단, 닉네임 변경)할 수 있음
    MEMBER,        // 일반 멤버(everyone)
}