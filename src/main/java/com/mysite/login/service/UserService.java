package com.mysite.login.service;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.entity.User;
import com.mysite.login.exception.DuplicateEmailException;
import com.mysite.login.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
        try {
            // 이메일 중복 검사 및 사용자 저장
            userRepository.findByEmail(userRequestDTO.getEmail())
                    .ifPresent(existingUser -> {
                        log.warn("이메일 중복 시도: {}", userRequestDTO.getEmail());
                        throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
                    });

            User user = User.builder()
                    .email(userRequestDTO.getEmail())
                    .password(passwordEncoder.encode(userRequestDTO.getPassword())) // 비밀번호 암호화
                    .build();

            userRepository.save(user);
            log.info("사용자 저장 성공: {}", user.getEmail());

        } catch (DataAccessException e) {
            log.error("데이터베이스 저장 중 오류 발생: {}", userRequestDTO.getEmail(), e);
            throw new DataAccessException("회원 가입 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e) {};
        }
    }

    // Spring Security 에서 사용자 정보를 가져오는 데 사용. email 로 사용자를 찾아서 UserDetails 타입으로 반환
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("loadUserByUsername 호출: {}", email); // loadUserByUsername 호출 로깅
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("사용자 찾을 수 없음: {}", email); // 사용자 찾을 수 없음 로깅
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                    });
        } catch (DataAccessException e) {
            log.error("사용자 조회 중 데이터베이스 오류 발생: {}", email, e);
            throw new DataAccessException("사용자 정보 조회 중 오류가 발생했습니다.", e) {};
        }
    }
}
