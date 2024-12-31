package com.mysite.login.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.info("Authentication failed with exception: {}", exception.getClass().getName());

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
        logger.info("Error message set in session: {}", errorMessage);
        response.sendRedirect("/login?error");
    }
}
