package com.sprint.mission.discodeit.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatusTest {

    @Test
    @DisplayName("마지막 활동 직후에는 온라인 상태")
    void isOnline_JustNow() {
        // Given
        UserStatus userStatus = new UserStatus(UUID.randomUUID());

        // When
        boolean result = userStatus.isOnline(Instant.now());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마지막 활동 5분 경과까지는 온라인 상태")
    void isOnline_AfterMinutes() {
        // Given
        UserStatus userStatus = new UserStatus(UUID.randomUUID());
        // 가상의 현재 시각: 유저가 활동한 시점(updatedAt)으로부터 3분 뒤
        Instant threeMinutesLater = userStatus.getUpdatedAt().plus(3, ChronoUnit.MINUTES);

        // When
        boolean result3 = userStatus.isOnline(threeMinutesLater);

        // Then
        assertThat(result3).isTrue();

        // ===

        // Given
        // 가상의 현재 시각: 유저가 활동한 시점(updatedAt)으로부터 정확히 5분 뒤
        Instant fiveMinutesLater = userStatus.getUpdatedAt().plus(5, ChronoUnit.MINUTES);

        // When
        boolean result5 = userStatus.isOnline(fiveMinutesLater);

        // Then
        assertThat(result5).isTrue();
    }

    @Test
    @DisplayName("마지막 활동 5분이 지나면 오프라인 상태")
    void isOnline_After6Minutes() {
        // Given
        UserStatus userStatus = new UserStatus(UUID.randomUUID());
        // 가상의 현재 시각: 유저가 활동한 시점(updatedAt)으로부터 6분 뒤
        Instant sixMinutesLater = userStatus.getUpdatedAt().plus(6, ChronoUnit.MINUTES);

        // When
        boolean result = userStatus.isOnline(sixMinutesLater);

        // Then
        assertThat(result).isFalse();
    }
}