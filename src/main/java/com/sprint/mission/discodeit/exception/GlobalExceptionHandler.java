package com.sprint.mission.discodeit.exception;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalAccessError.class)
    public String handleIllegalArgument(IllegalArgumentException e,
                                        HttpServletRequest request,
                                        Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "잘못된 요청입니다.");
        model.addAttribute("message", e.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error/custom-error"; // templates/error/custom-error.html
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e,
                                  HttpServletRequest request,
                                  Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "내부 서버 오류");
        model.addAttribute("message", e.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error/custom-error";
    }
}
