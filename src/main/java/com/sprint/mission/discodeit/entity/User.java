package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class User extends BaseUpdatableEntity {


    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @OneToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ReadStatus> readStatusList = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Message> messageList = new ArrayList<>(); // 특정 유저가 생성한 모든 메시지


    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private UserStatus status;

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
        updateUpdatedAt(Instant.now());
    }

    public void updateUsername(String username) {
        this.username = username;
        updateUpdatedAt(Instant.now());
    }

    public void updateEmail(String email) {
        this.email = email;
        updateUpdatedAt(Instant.now());
    }

    public void updatePassword(String password) {
        this.password = password;
        updateUpdatedAt(Instant.now());
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
        if (status.getUser() == null) {
            status.updateUser(this);
        }
    }


    public void addMessage(Message message) {
        this.messageList.add(message);
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", readStatusList=" + readStatusList +
            ", messageList=" + messageList +
            ", profile=" + profile +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
