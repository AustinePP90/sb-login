package com.mysite.login.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getDetails() != null) {
            try {
                // 현재 세션 무효화
                request.getSession().invalidate();
                log.info("사용자 '{}' 로그아웃 성공", authentication.getName());
            } catch (Exception e) {
                log.error("로그아웃 처리 중 에러 발생", e);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/login");
    }
}
