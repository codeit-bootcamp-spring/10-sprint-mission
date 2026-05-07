package com.codeit.mission.deokhugam.dashboard.controller;

import com.codeit.mission.deokhugam.dashboard.batch.DashboardBatchScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@Profile("dev") // dev 프로파일로 실행할 때에만 활성화 됨
public class BatchTestController {
  private final DashboardBatchScheduler dashboardBatchScheduler;

  @PostMapping("/api/dashboard/aggregate")
  public void executeScheduler(){
    dashboardBatchScheduler.runDashboardAggregation();
  }
}
