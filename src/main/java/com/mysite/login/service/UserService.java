package com.mysite.login.service;

import com.mysite.login.dto.UserRequestDTO;
import com.mysite.login.entity.User;
import com.mysite.login.exception.DuplicateEmailException;
import com.mysite.login.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserRequestDTO userRequestDTO) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword())) // 비밀번호 암호화
                .build();

        userRepository.save(user);
    }

    // Spring Security 에서 사용자 정보를 가져오는 데 사용. email 로 사용자를 찾아서 UserDetails 타입으로 반환
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
