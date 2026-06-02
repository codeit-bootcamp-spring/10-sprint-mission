package com.sprint.mission.discodeit.logging; // util이나 support 패키지 추천

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LogContentExtractor {

    private static final int MAX_LOG_LENGTH = 500; // 로그 길이 제한

    // 1. Request Body 추출 및 가공
    public String extractRequestBody(HttpServletRequest request) {
        // 일반 JSON 요청
        ContentCachingRequestWrapper cachingRequest = WebUtils.getNativeRequest(request,
                ContentCachingRequestWrapper.class);
        if (cachingRequest != null && cachingRequest.getContentAsByteArray().length > 0) {
            String body = new String(cachingRequest.getContentAsByteArray(),
                    StandardCharsets.UTF_8);
            return formatBody(body);
        }

        // 멀티파트 요청 처리
        if (request.getContentType() != null && request.getContentType()
                .startsWith("multipart/form-data")) {
            return extractMultipartBody(request);
        }

        return "";
    }

    // 2. Response Body 추출 및 가공
    public String extractResponseBody(HttpServletResponse response, HttpServletRequest request) {
        // 다운로드 API 생략
        if (request.getRequestURI().contains("/download") && response.getStatus() < 400) {
            return "[File Download Completed - Body Omitted]";
        }

        ContentCachingResponseWrapper cachingResponse = WebUtils.getNativeResponse(response,
                ContentCachingResponseWrapper.class);
        if (cachingResponse != null && cachingResponse.getContentAsByteArray().length > 0) {
            String contentType = cachingResponse.getContentType();

            // JSON/텍스트인 경우
            if (contentType != null && (contentType.startsWith("application/json")
                    || contentType.startsWith("text/"))) {
                String body = new String(cachingResponse.getContentAsByteArray(),
                        StandardCharsets.UTF_8);
                return formatBody(body);
            }
            // 그 외 바이너리인 경우
            return "[Binary Data] Type: " + contentType + ", Size: "
                    + cachingResponse.getContentAsByteArray().length + " bytes";
        }
        return "";
    }

    // --- 내부 헬퍼 메서드 (Formatting & Multipart Parsing) ---
    private String formatBody(String body) {
        String formatted = body.replaceAll("[\r\n\\s]+", " ");
        if (formatted.length() > MAX_LOG_LENGTH) {
            return formatted.substring(0, MAX_LOG_LENGTH) + "... [TRUNCATED]";
        }
        return formatted;
    }

    private String extractMultipartBody(HttpServletRequest request) {
        StringBuilder multipartLog = new StringBuilder("Multipart Data: ");
        try {
            for (jakarta.servlet.http.Part part : request.getParts()) {
                String contentType = part.getContentType();
                boolean isTextData =
                        contentType != null && (contentType.startsWith("application/json")
                                || contentType.startsWith("text/"));

                if (part.getSubmittedFileName() == null || isTextData) {
                    String partValue = new String(part.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8);
                    multipartLog.append("[").append(part.getName()).append("=")
                            .append(formatBody(partValue)).append("] ");
                } else {
                    multipartLog.append("[").append(part.getName()).append("=(File: ")
                            .append(part.getSubmittedFileName())
                            .append(", Size: ").append(part.getSize()).append(" bytes)] ");
                }
            }
            return multipartLog.toString();
        } catch (Exception e) {
            log.error("Multipart parsing error", e);
            return "Error reading multipart body";
        }
    }
}