package com.mysite.login.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 이메일 중복 예외 처리
    @ExceptionHandler(DuplicateEmailException.class)
    public ModelAndView handleDuplicateEmailException(DuplicateEmailException e) {
        log.warn("이메일 중복 예외 발생: {}", e.getMessage()); // 이메일 중복 예외 로깅
        ModelAndView modelAndView = new ModelAndView("signup"); // 에러 발생 시 보여줄 뷰
        modelAndView.addObject("errorMessage", e.getMessage()); // 에러 메시지를 모델에 추가
        return modelAndView;
    }

    // 데이터베이스 관련 예외 처리
    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDataAccessException(DataAccessException e) {
        log.error("데이터베이스 오류", e);
        ModelAndView modelAndView = new ModelAndView("error"); // 에러 페이지
        modelAndView.addObject("errorMessage", "데이터베이스 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."); // 사용자에게 보여줄 에러 메시지
        return modelAndView;
    }

    // 메서드에 잘못된 인자 전달 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("잘못된 인자 전달", e);
        ModelAndView modelAndView = new ModelAndView("error"); // 에러 페이지 (상황에 맞게 변경)
        modelAndView.addObject("errorMessage", "잘못된 요청입니다."); // 사용자에게 예외 메시지 전달 (보안에 주의)
        return modelAndView;
    }

    // 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("예상치 못한 오류 발생", e);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return modelAndView;
    }
}
