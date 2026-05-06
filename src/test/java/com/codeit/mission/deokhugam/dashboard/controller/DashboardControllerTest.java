package com.codeit.mission.deokhugam.dashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.batch.DashboardBatchScheduler;
import com.codeit.mission.deokhugam.dashboard.popularbooks.controller.PopularBookController;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.service.PopularBookService;
import com.codeit.mission.deokhugam.dashboard.popularreviews.controller.PopularReviewController;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.CursorPageResponsePopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.service.PopularReviewService;
import com.codeit.mission.deokhugam.dashboard.powerusers.controller.PowerUserController;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.CursorPageResponsePowerUserDto;
import com.codeit.mission.deokhugam.dashboard.powerusers.service.PowerUserService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DashboardControllerTest {

  @Test
  @DisplayName("popular book controller uses default query parameters")
  void popularBookController_defaultParameters() throws Exception {
    PopularBookService service = mock(PopularBookService.class);
    when(service.get(any(), any(), any(), any(), eq(50)))
        .thenReturn(new CursorPageResponsePopularBookDto(List.of(), null, null, 50, 0, false));
    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new PopularBookController(service))
        .build();

    mockMvc.perform(get("/api/books/popular"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(50))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.hasNext").value(false));

    verify(service).get(PeriodType.DAILY, DirectionEnum.ASC, null, null, 50);
  }

  @Test
  @DisplayName("popular book controller forwards custom query parameters")
  void popularBookController_customParameters() throws Exception {
    PopularBookService service = mock(PopularBookService.class);
    when(service.get(any(), any(), any(), any(), eq(20)))
        .thenReturn(new CursorPageResponsePopularBookDto(List.of(), "2", "2026-04-27T00:00:00Z",
            20, 10, true));
    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new PopularBookController(service))
        .build();

    mockMvc.perform(get("/api/books/popular")
            .param("period", "WEEKLY")
            .param("direction", "DESC")
            .param("cursor", "2")
            .param("after", "2026-04-27T00:00:00Z")
            .param("limit", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nextCursor").value("2"))
        .andExpect(jsonPath("$.hasNext").value(true));

    verify(service).get(
        PeriodType.WEEKLY, DirectionEnum.DESC, "2", "2026-04-27T00:00:00Z", 20);
  }

  @Test
  @DisplayName("popular review controller forwards query parameters")
  void popularReviewController() throws Exception {
    PopularReviewService service = mock(PopularReviewService.class);
    when(service.getReviews(any(), any(), any(), any(), eq(30)))
        .thenReturn(new CursorPageResponsePopularReviewDto(List.of(), null, null, 30, 0, false));
    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new PopularReviewController(service))
        .build();

    mockMvc.perform(get("/api/reviews/popular")
            .param("period", "MONTHLY")
            .param("direction", "ASC")
            .param("limit", "30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(30));

    verify(service).getReviews(PeriodType.MONTHLY, DirectionEnum.ASC, null, null, 30);
  }

  @Test
  @DisplayName("power user controller forwards query parameters")
  void powerUserController() throws Exception {
    PowerUserService service = mock(PowerUserService.class);
    when(service.getLatestRankings(any(), any(), any(), any(), eq(15)))
        .thenReturn(new CursorPageResponsePowerUserDto(List.of(), "1", "2026-04-27T00:00:00Z",
            15, 40, true));
    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new PowerUserController(service))
        .build();

    mockMvc.perform(get("/api/users/power")
            .param("period", "ALL_TIME")
            .param("direction", "DESC")
            .param("cursor", "1")
            .param("after", "2026-04-27T00:00:00Z")
            .param("size", "15"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(40));

    verify(service).getLatestRankings(
        PeriodType.ALL_TIME, DirectionEnum.DESC, "1", "2026-04-27T00:00:00Z", 15);
  }

  @Test
  @DisplayName("batch test controller executes dashboard scheduler")
  void batchTestController() throws Exception {
    DashboardBatchScheduler scheduler = mock(DashboardBatchScheduler.class);
    MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new BatchTestController(scheduler))
        .build();

    mockMvc.perform(post("/api/dashboard/aggregate"))
        .andExpect(status().isOk());

    verify(scheduler).runDashboardAggregation();
  }
}
