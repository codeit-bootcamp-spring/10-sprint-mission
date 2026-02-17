package com.sprint.mission.discodeit.exceptionhandler;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public String getErrorPage (RuntimeException e, HttpServletRequest req, Model model, int statusCode, String message){
        model.addAttribute("status", statusCode);
        model.addAttribute("error", message);
        model.addAttribute("message", e.getMessage());
        model.addAttribute("path", req.getRequestURI());

        return "error/custom-error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStatus(IllegalStateException e,
                                      HttpServletRequest request,
                                      Model model){
        return getErrorPage(e, request, model, 400, "잘못된 상태입니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e,
                                                   HttpServletRequest request,
                                                   Model model){
        return getErrorPage(e, request, model, 400, "유효하지 않은 형태의 매개변수입니다.");

    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElement(NoSuchElementException e,
                                      HttpServletRequest request,
                                      Model model){
        return getErrorPage(e, request, model, 400, "해당 원소를 찾을 수 없습니다.");
    }
}
