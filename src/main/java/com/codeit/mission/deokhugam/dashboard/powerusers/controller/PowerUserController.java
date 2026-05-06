package com.codeit.mission.deokhugam.dashboard.powerusers.controller;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.CursorPageResponsePowerUserDto;
import com.codeit.mission.deokhugam.dashboard.powerusers.service.PowerUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대시보드 관리")
@RestController
@RequestMapping("/api/users/power")
@RequiredArgsConstructor
public class PowerUserController {


  private final PowerUserService powerUserService;

  // 집계된 파워 유저의 DTO를 목록화해서 응답함
  // 파라미터로 기간, cursor, size가 들어감
  // cursor를 공백으로 둘 시 첫 페이지 반환되고 size는 기본값이 50
  @Operation(
      summary = "파워 유저 목록 조회",
      operationId = "find_power_user_3",
      description = "기간별 파워 유저 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "파워 유저 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @GetMapping
  public ResponseEntity<CursorPageResponsePowerUserDto> getPowerUsers(
      @RequestParam(defaultValue = "DAILY") PeriodType period, // 기간 (일간, 주간, 월간, 올타임)
      @RequestParam(defaultValue = "ASC") DirectionEnum direction, // 정렬 방향
      @RequestParam(value = "cursor", required = false) String cursor, // 커서는 필수 값이 아님.
      @RequestParam(value = "after", required = false) String after,
      @RequestParam(defaultValue = "50") int limit) { // 페이지의 사이즈는 기본값이 50

    // ResponseEntity는 ok를 보내고 body값으로 slice된 PowerUserDto 들을 반환함.
    return ResponseEntity.ok(
        powerUserService.getLatestRankings(period, direction, cursor, after, limit));
  }
}
