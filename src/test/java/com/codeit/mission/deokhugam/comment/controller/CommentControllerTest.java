package com.codeit.mission.deokhugam.comment.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codeit.mission.deokhugam.comment.dto.request.CommentCreateRequest;
import com.codeit.mission.deokhugam.comment.dto.request.CommentFindAllRequest;
import com.codeit.mission.deokhugam.comment.dto.request.CommentUpdateRequest;
import com.codeit.mission.deokhugam.comment.dto.response.CommentDto;
import com.codeit.mission.deokhugam.comment.dto.response.CursorPageResponseCommentDto;
import com.codeit.mission.deokhugam.comment.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글을 등록한다")
    void createComment_success() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        Instant now = Instant.now();

        CommentCreateRequest request = new CommentCreateRequest(
                reviewId,
                userId,
                "댓글 내용"
        );

        CommentDto response = new CommentDto(
                commentId,
                reviewId,
                userId,
                "테스트유저",
                "댓글 내용",
                now,
                now
        );

        given(commentService.createComment(any(CommentCreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.reviewId").value(reviewId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.userNickname").value("테스트유저"))
                .andExpect(jsonPath("$.content").value("댓글 내용"));

        verify(commentService).createComment(any(CommentCreateRequest.class));
    }

    @Test
    @DisplayName("댓글 등록 시 content가 null이면 400을 반환한다")
    void createComment_invalidRequest_fail() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CommentCreateRequest request = new CommentCreateRequest(
                reviewId,
                userId,
                null
        );

        // when & then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateComment_success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        UUID requestUserId = UUID.randomUUID();
        Instant now = Instant.now();

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        CommentDto response = new CommentDto(
                commentId,
                reviewId,
                requestUserId,
                "테스트유저",
                "수정된 댓글",
                now,
                now
        );

        given(commentService.updateComment(
                eq(commentId),
                eq(requestUserId),
                any(CommentUpdateRequest.class)
        )).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                        .header("Deokhugam-Request-User-ID", requestUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("수정된 댓글"));

        verify(commentService).updateComment(
                eq(commentId),
                eq(requestUserId),
                any(CommentUpdateRequest.class)
        );
    }

    @Test
    @DisplayName("댓글 수정 시 요청자 헤더가 없으면 400을 반환한다")
    void updateComment_missingRequestUserId_fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when & then
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_REQUEST_HEADER"))
                .andExpect(jsonPath("$.details.header").value("Deokhugam-Request-User-ID"));
    }

    @Test
    @DisplayName("댓글 상세 정보를 조회한다")
    void getComment_success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        CommentDto response = new CommentDto(
                commentId,
                reviewId,
                userId,
                "테스트유저",
                "댓글 내용",
                now,
                now
        );

        given(commentService.findComment(commentId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/comments/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("댓글 내용"));

        verify(commentService).findComment(commentId);
    }

    @Test
    @DisplayName("댓글 목록을 조회한다")
    void getComments_success() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        CommentDto commentDto = new CommentDto(
                commentId,
                reviewId,
                userId,
                "테스트유저",
                "댓글 내용",
                now,
                now
        );

        CursorPageResponseCommentDto response = new CursorPageResponseCommentDto(
                List.of(commentDto),
                commentId.toString(),
                now,
                1,
                1L,
                false
        );

        given(commentService.findAllComments(any(CommentFindAllRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/comments")
                        .param("reviewId", reviewId.toString())
                        .param("direction", "ASC")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("댓글 내용"))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));

        ArgumentCaptor<CommentFindAllRequest> captor =
                ArgumentCaptor.forClass(CommentFindAllRequest.class);

        verify(commentService).findAllComments(captor.capture());

        CommentFindAllRequest captured = captor.getValue();
        assertThat(captured.reviewId()).isEqualTo(reviewId);
        assertThat(captured.direction()).isEqualTo("ASC");
        assertThat(captured.limit()).isEqualTo(10);
    }

    @Test
    @DisplayName("댓글 목록 조회 시 reviewId가 없으면 400을 반환한다")
    void getComments_missingReviewId_fail() throws Exception {
        // when & then
        mockMvc.perform(get("/api/comments")
                        .param("direction", "ASC")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 목록 조회 시 direction이 ASC 또는 DESC가 아니면 400을 반환한다")
    void getComments_invalidDirection_fail() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/comments")
                        .param("reviewId", reviewId.toString())
                        .param("direction", "INVALID")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 목록 조회 시 limit이 1 미만이면 400을 반환한다")
    void getComments_invalidLimit_fail() throws Exception {
        // given
        UUID reviewId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/comments")
                        .param("reviewId", reviewId.toString())
                        .param("direction", "ASC")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글을 논리 삭제한다")
    void deleteComment_success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID requestUserId = UUID.randomUUID();

        doNothing().when(commentService).softDelete(commentId, requestUserId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isNoContent());

        verify(commentService).softDelete(commentId, requestUserId);
    }

    @Test
    @DisplayName("댓글 논리 삭제 시 요청자 헤더가 없으면 400을 반환한다")
    void deleteComment_missingRequestUserId_fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_REQUEST_HEADER"))
                .andExpect(jsonPath("$.details.header").value("Deokhugam-Request-User-ID"));
    }

    @Test
    @DisplayName("댓글을 물리 삭제한다")
    void hardDeleteComment_success() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();
        UUID requestUserId = UUID.randomUUID();

        doNothing().when(commentService).hardDelete(commentId, requestUserId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
                        .header("Deokhugam-Request-User-ID", requestUserId))
                .andExpect(status().isNoContent());

        verify(commentService).hardDelete(commentId, requestUserId);
    }

    @Test
    @DisplayName("댓글 물리 삭제 시 요청자 헤더가 없으면 400을 반환한다")
    void hardDeleteComment_missingRequestUserId_fail() throws Exception {
        // given
        UUID commentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_REQUEST_HEADER"))
                .andExpect(jsonPath("$.details.header").value("Deokhugam-Request-User-ID"));
    }
}