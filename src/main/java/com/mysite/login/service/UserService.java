package com.mysite.login.service;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.entity.User;
import com.mysite.login.exception.DuplicateEmailException;
import com.mysite.login.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserRequestDTO userRequestDTO) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            log.warn("이메일 중복 시도: {}", userRequestDTO.getEmail()); // 이메일 중복 시도 로깅
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword())) // 비밀번호 암호화
                .build();

        userRepository.save(user);
        log.info("사용자 저장 성공: {}", user.getEmail()); // 사용자 저장 성공 로깅
    }

    // Spring Security 에서 사용자 정보를 가져오는 데 사용. email 로 사용자를 찾아서 UserDetails 타입으로 반환
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("loadUserByUsername 호출: {}", email); // loadUserByUsername 호출 로깅
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("사용자 찾을 수 없음: {}", email); // 사용자 찾을 수 없음 로깅
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });
    }
}
