package com.mysite.login.controller;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.entity.User;
import com.mysite.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<String> signup (@RequestBody UserRequestDTO userRequestDTO) {
        userService.createUser(userRequestDTO);
        return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
    }
}
