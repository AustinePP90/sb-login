package com.mysite.login.controller;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.exception.DuplicateEmailException;
import com.mysite.login.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup (@Valid @ModelAttribute UserRequestDTO userRequestDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signup"; // 유효성 검사 실패 시 signup 페이지로 forward
        }

        try {
            userService.createUser(userRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다.");
            return "redirect:/login";
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "signup";
        }
    }
}
