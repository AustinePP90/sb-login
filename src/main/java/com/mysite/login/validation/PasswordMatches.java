package com.mysite.login.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ElementType.TYPE}) // 클래스 레벨에서 검증
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}