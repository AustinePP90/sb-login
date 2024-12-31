package com.mysite.login.config;

import com.mysite.login.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration // 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security 설정을 활성화
public class SecurityConfig {

    private final UserService userService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(@Lazy UserService userService,
                          CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                          CustomLogoutSuccessHandler customLogoutSuccessHandler,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userService = userService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    // HTTP 요청에 대한 보안 설정을 함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable()) // 개발 단계에서 임시로 비활성화, 운영 시에는 활성화 고려
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(antMatcher("/signup"), antMatcher("/login"), antMatcher(("/"))).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureHandler(customAuthenticationFailureHandler)
                        .successHandler(customAuthenticationSuccessHandler) // 로그인 성공 핸들러 등록
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login")
                        .logoutSuccessHandler(customLogoutSuccessHandler) // 로그아웃 성공 핸들러 등록
                        .invalidateHttpSession(true) // 로그아웃 시 HTTP 세션 무효화
                        .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 삭제 (선택 사항)
                        .permitAll()
                );
        return http.build();
    }

    // 비밀번호 암호화에 사용할 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자 인증을 처리할 AuthenticationProvider Bean 등록, DaoAuthenticationProvider 를 사용하여 데이터베이스에서 사용자 정보를 가져와 인증
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // 인증 매니저 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Valid 어노테이션 활성화를 위한 Bean 등록
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
