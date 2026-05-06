package com.codeit.mission.deokhugam.dashboard.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.exceptions.InvalidJobParameterException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JobParameterUtilsTest {

  @Test
  @DisplayName("validateRequired accepts non-blank parameter values")
  void validateRequired_acceptsPresentValues() {
    JobParameterUtils.validateRequired(
        JobParameterUtils.parameter("domainType", "POPULAR_BOOK"),
        JobParameterUtils.parameter("snapshotId", UUID.randomUUID().toString()));
  }

  @Test
  @DisplayName("validateRequired throws when any parameter is missing or blank")
  void validateRequired_rejectsMissingValues() {
    assertThatThrownBy(
        () -> JobParameterUtils.validateRequired(
            JobParameterUtils.parameter("domainType", null),
            JobParameterUtils.parameter("snapshotId", " ")))
        .isInstanceOf(InvalidJobParameterException.class);
  }

  @Test
  @DisplayName("validateRequired throws when parameter value is blank")
  void validateRequired_rejectsBlankValue() {
    assertThatThrownBy(
        () -> JobParameterUtils.validateRequired(
            JobParameterUtils.parameter("domainType", " ")))
        .isInstanceOf(InvalidJobParameterException.class);
  }

  @Test
  @DisplayName("parseUuid parses valid uuid value")
  void parseUuid_valid() {
    UUID uuid = UUID.randomUUID();

    UUID result = JobParameterUtils.parseUuid("snapshotId", uuid.toString());

    assertThat(result).isEqualTo(uuid);
  }

  @Test
  @DisplayName("parseUuid throws for invalid uuid value")
  void parseUuid_invalid() {
    assertThatThrownBy(() -> JobParameterUtils.parseUuid("snapshotId", "not-a-uuid"))
        .isInstanceOf(InvalidJobParameterException.class);
  }

  @Test
  @DisplayName("parseInstant parses valid instant value")
  void parseInstant_valid() {
    Instant instant = Instant.parse("2026-04-27T00:00:00Z");

    Instant result = JobParameterUtils.parseInstant("aggregatedAt", instant.toString());

    assertThat(result).isEqualTo(instant);
  }

  @Test
  @DisplayName("parseInstant throws for blank value")
  void parseInstant_blank() {
    assertThatThrownBy(() -> JobParameterUtils.parseInstant("aggregatedAt", " "))
        .isInstanceOf(InvalidJobParameterException.class);
  }

  @Test
  @DisplayName("parseEnum parses enum name")
  void parseEnum_valid() {
    DomainType result =
        JobParameterUtils.parseEnum("domainType", "POWER_USER", DomainType.class);

    assertThat(result).isEqualTo(DomainType.POWER_USER);
  }

  @Test
  @DisplayName("parseEnum throws for unknown enum name")
  void parseEnum_invalid() {
    assertThatThrownBy(
        () -> JobParameterUtils.parseEnum("domainType", "UNKNOWN", DomainType.class))
        .isInstanceOf(InvalidJobParameterException.class);
  }
}
