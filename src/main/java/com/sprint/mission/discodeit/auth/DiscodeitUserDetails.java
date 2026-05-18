package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.userDto.getRole()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return userDto.getEmail();
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.userDto.getId());
    }

    @Override
    public boolean equals(Object obj) {
        // 주소 값이 아예 같으면 무조건 같으므로 true
        if(this == obj) return true;

        // 객체가 null이거나
        if(obj == null || getClass() != obj.getClass()) return false;

        DiscodeitUserDetails that = (DiscodeitUserDetails) obj;
        return Objects.equals(that.getUserDto().getId(), this.userDto.getId());
    }
}
