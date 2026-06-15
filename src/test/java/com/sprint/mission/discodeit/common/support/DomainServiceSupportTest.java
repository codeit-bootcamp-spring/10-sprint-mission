package com.sprint.mission.discodeit.common.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;

class DomainServiceSupportTest {

  private static final ErrorCode TEST_ERROR_CODE = new ErrorCode() {
    @Override
    public HttpStatus getStatus() {
      return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getCode() {
      return "TEST_ERROR";
    }

    @Override
    public String getMessage() {
      return "Test error message";
    }
  };

  @Test
  @DisplayName("getOrThrow - 값이 존재하면 해당 값을 반환한다.")
  void getOrThrow_success() {
    String expected = "result";
    String result = DomainServiceSupport.getOrThrow(
        "input",
        input -> Optional.of(expected),
        input -> new DiscodeitException(TEST_ERROR_CODE) {}
    );

    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("getOrThrow - 값이 없으면 예외가 발생한다.")
  void getOrThrow_throwsException() {
    assertThatThrownBy(() -> DomainServiceSupport.getOrThrow(
        "input",
        input -> Optional.empty(),
        input -> new DiscodeitException(TEST_ERROR_CODE) {}
    ))
        .isInstanceOf(DiscodeitException.class)
        .hasMessageContaining("Test error message");
  }

  @Test
  @DisplayName("deleteOrThrow - ID에 해당하는 엔티티가 있으면 삭제한다.")
  void deleteOrThrow_success() {
    JpaRepository<Object, UUID> repository = mock(JpaRepository.class);
    UUID id = UUID.randomUUID();
    Object entity = new Object();
    given(repository.findById(id)).willReturn(Optional.of(entity));

    DomainServiceSupport.deleteOrThrow(id, repository, input -> new DiscodeitException(TEST_ERROR_CODE) {});

    verify(repository).delete(entity);
  }

  @Test
  @DisplayName("deleteOrThrow - ID에 해당하는 엔티티가 없으면 예외가 발생한다.")
  void deleteOrThrow_throwsException() {
    JpaRepository<Object, UUID> repository = mock(JpaRepository.class);
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> DomainServiceSupport.deleteOrThrow(
        id, repository, input -> new DiscodeitException(TEST_ERROR_CODE) {}
    ))
        .isInstanceOf(DiscodeitException.class)
        .hasMessageContaining("Test error message");
  }

  @Test
  @DisplayName("requireOrThrow - 조건이 참이면 아무 일도 일어나지 않는다.")
  void requireOrThrow_success() {
    DomainServiceSupport.requireOrThrow(
        "input",
        input -> true,
        input -> new DiscodeitException(TEST_ERROR_CODE) {}
    );
  }

  @Test
  @DisplayName("requireOrThrow - 조건이 거짓이면 예외가 발생한다.")
  void requireOrThrow_throwsException() {
    assertThatThrownBy(() -> DomainServiceSupport.requireOrThrow(
        "input",
        input -> false,
        input -> new DiscodeitException(TEST_ERROR_CODE) {}
    ))
        .isInstanceOf(DiscodeitException.class)
        .hasMessageContaining("Test error message");
  }
}
