package com.mysite.login.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.info("Authentication failed with exception: {}", exception.getClass().getName());

        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "이메일 또는 비밀번호가 정확하지 않습니다.";
//        } else if (exception instanceof org.springframework.security.authentication.DisabledException) {
//            errorMessage = "계정이 비활성화되었습니다.";
//        } else if (exception instanceof org.springframework.security.authentication.LockedException) {
//            errorMessage = "계정이 잠금 처리되었습니다.";
        } else {
            errorMessage = "로그인에 실패하였습니다. 다시 시도해주세요.";
        }

        request.getSession().setAttribute("errorMessage", errorMessage);
        log.info("로그인 실패: {}", errorMessage);
        response.sendRedirect("/login?error");
    }
}
