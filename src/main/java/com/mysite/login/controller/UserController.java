package com.mysite.login.controller;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.exception.DuplicateEmailException;
import com.mysite.login.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup (@Valid @ModelAttribute UserRequestDTO userRequestDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        log.info("회원가입 요청: {}", userRequestDTO.getEmail()); // 회원가입 요청 로깅

        // PasswordMatches 어노테이션 검증 후, 오류가 있다면 confirmPassword 필드에 추가
        if (bindingResult.hasGlobalErrors()) {
            bindingResult.getGlobalErrors().forEach(error -> {
                if (error.getCode().equals("PasswordMatches")) { // PasswordMatches 어노테이션의 기본 code 값
                    bindingResult.rejectValue("confirmPassword", "PasswordMatches", error.getDefaultMessage());
                }
            });
        }

        if (bindingResult.hasErrors()) {
            log.warn("회원가입 유효성 검사 실패: {}", bindingResult.getAllErrors()); // 유효성 검사 실패 로깅
            return "signup"; // 유효성 검사 실패 시 signup 페이지로 forward
        }

        try {
            userService.createUser(userRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다.");
            log.info("회원가입 성공: {}", userRequestDTO.getEmail()); // 회원가입 성공 로깅
            return "redirect:/login";
        } catch (DuplicateEmailException e) {
            log.error("회원가입 실패 - 이메일 중복: {}", userRequestDTO.getEmail()); // 이메일 중복 에러 로깅
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "signup";
        }
    }
}
