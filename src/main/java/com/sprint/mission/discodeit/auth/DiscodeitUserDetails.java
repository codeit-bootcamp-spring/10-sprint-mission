package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;

    /**
    GrantedAuthority는 Spring Security에서 말하는 "권한 1개"를 의미한다.
    enum으로 권한 USER, ADMIN, CHANNEL_MANAGER 가 있으면 Spring Security는 enum 그대로 쓰지않고, GrantedAuthority 객체로 들고있는다.

    GrantedAuthority 내부 getAuthority()는 "이 사용자는 어떤 권한 문자열을 가지고 있나?"를 반환하는 타입
     new SimpleGrantedAuthority("ROLE_ADMIN") 이렇게 하면 "ROLE_ADMIN"이라는 구너한 문자열을 가진 객체..
    **/

    //인증된 사용자의 권한
    //Spring Security에서는 role 권한을 "ROLE_" prefix 붙여서 넣는다.
    //ROLE_ 붙여서 넣어두면 hasRole("ADMIN")사용 가능: 내부적으로 "ROLE_ADMIN"을 찾는다.
    //@PreAuthorize("hasRole('ADMIN')")로 서버에서 권한체크 쉽게 할 수 있다.
    //OR SecurityConfig에서 .requestMatchers("/api/auth/role").hasRole("ADMIN")로 사용 가능.

    //UserDetailsService에서 UserDetails 만들어지고, 인증되면, Security Context에 ROLE_붙여서 저장됨.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
    }

    //Spring Security에서 사용자를 식별하는 이름.
    //로그인 ID 역할을 한다. -> discodeit에서는 로그인을 사용자 이름으로 한다.
    @Override
    public String getUsername() {
        return userDto.username();
    }


    /** =====계정 상태===== **/
    //계정이 만료되지않았는지
    //true: 계정 사용가능
    //false: 계정 만료, 로그인 실패
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겨 있지 않은지
    //true: 잠기지 않음
    //false: 잠긴계정, 로그인 실패
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 같은 인증 정보가 만료되지 않았는지
    //true: 비밀번호 사용가능
    //false: 비밀번호 만료, 로그인 실패
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 활성화되어 있는지
    //true: 활성 계정
    //false: 비활성 계정, 로그인 실패
    @Override
    public boolean isEnabled() {
        return true;
    }
    /**
     DiscodeitUserDetails 객체끼리 동등성 비교
     기본 equals는 주소값 비교인데 여기서는 사용자 기준으로 비교.

     (1) 예시
     UserDetailsService에서는 로그인할때마다 새로운 객체를 만든다.
     DiscodeitUserDetails A = 로그인1 사용자
     DiscodeitUserDetails B = 로그인2 사용자

     // userDto.id()가 같으면 같은 사용자
     A.equals(B) == true

     그래야 sessionConcurrency().maximumSessions(1)이 의도대로 동작한다.
     **/

    @Override
    public boolean equals(Object o) {
        //메모리 주소 자체가 같은지 비교
        if (this == o) return true;

        //이 객체가 DiscodeitUserDetails 타입인지 확인
        if (!(o instanceof DiscodeitUserDetails that)) return false;

        //현재 객체 user id == 비교 대상 객체의 user id -> true
        return Objects.equals(userDto.id(), that.userDto.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDto.id());
    }
}
