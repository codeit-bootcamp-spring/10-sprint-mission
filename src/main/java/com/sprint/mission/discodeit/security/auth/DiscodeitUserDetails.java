package com.sprint.mission.discodeit.security.auth;

import com.sprint.mission.discodeit.dto.response.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
    DiscodeitUserDetails
    --------------------
    인증을 마친 사용자 정보
 */
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    // 사용자 권한 (Role) 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + userDto.role().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return this.password;
    }

    // 사용자 닉네임 반환
    @Override
    public String getUsername() {
        return userDto.username();
    }

    // 게정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;        // 만료 안 됨
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;        // 잠기지 않음
    }

    // 비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;            // 만료 안 됨
    }

    // 계정 활성화 여부 반환
    @Override
    public boolean isEnabled() {
        return true;        // 활성화
    }

    // 객체 비교
    @Override
    public boolean equals(Object o) {
        // 메모리 주소 비교
        if (this == o) {
            return true;
        }

        // 널이거나 클래스 타입이 다를 경우, 불일치 반환
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        // 형 변환
        DiscodeitUserDetails that = (DiscodeitUserDetails) o;

        // 사용자 닉네임이 동일한 경우, 같은 객체로 취급
        return Objects.equals(this.getUsername(), that.getUsername());
    }

    // 해시값 생성
    @Override
    public int hashCode() {
        // 사용자 닉네임을 기반으로 해시값 생성
        return Objects.hash(this.getUsername());
    }
}
