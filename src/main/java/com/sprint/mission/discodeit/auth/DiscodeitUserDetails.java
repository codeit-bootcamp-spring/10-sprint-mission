package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;

    //사용자의 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
}
