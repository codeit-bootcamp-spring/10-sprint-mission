package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserStatus status;

    @OneToMany(mappedBy = "author")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public User(
            String username,
            String password,
            String email
    ) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
        markUpdated();
    }

    public void update(
            String username,
            String password,
            String email,
            BinaryContent profile
    ) {
        Optional.ofNullable(username)
                .ifPresent(value -> this.username = value);
        Optional.ofNullable(password)
                .ifPresent(value -> this.password = value);
        Optional.ofNullable(email)
                .ifPresent(value -> this.email = value);
        Optional.ofNullable(profile)
                .ifPresent(value -> this.profile = value);

        markUpdated();
    }

    public void assignStatus(UserStatus status) {
        if (this.status == status) {
            return;
        }

        UserStatus previousStatus = this.status;
        this.status = status;

        if (previousStatus != null && previousStatus.getUser() == this) {
            previousStatus.clearUser();
        }
        if (status != null && status.getUser() != this) {
            status.assignUser(this);
        }
    }

    public void addMessage(Message message) {
        if (message == null) {
            return;
        }
        if (!messages.contains(message)) {
            messages.add(message);
        }
        if (message.getAuthor() != this) {
            message.assignAuthor(this);
        }
    }

    public void removeMessage(Message message) {
        if (message == null) {
            return;
        }
        messages.remove(message);
        if (message.getAuthor() == this) {
            message.clearAuthor();
        }
    }

    public void addReadStatus(ReadStatus readStatus) {
        if (readStatus == null) {
            return;
        }
        if (!readStatuses.contains(readStatus)) {
            readStatuses.add(readStatus);
        }
        if (readStatus.getUser() != this) {
            readStatus.assignUser(this);
        }
    }

    public void removeReadStatus(ReadStatus readStatus) {
        if (readStatus == null) {
            return;
        }
        readStatuses.remove(readStatus);
        if (readStatus.getUser() == this) {
            readStatus.clearUser();
        }
    }
}
