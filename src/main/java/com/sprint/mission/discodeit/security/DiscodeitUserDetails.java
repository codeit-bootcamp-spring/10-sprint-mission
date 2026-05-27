package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
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
    private final UserDTO userDTO;
    private final String password;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDTO.role().name()));
    }

    @Override
    public String getUsername() {
        return userDTO.username();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDTO.id());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DiscodeitUserDetails other)) return false;
        return Objects.equals(this.userDTO.id(), other.userDTO.id());
    }
}
