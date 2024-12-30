package com.mysite.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity // JPA 엔티티임을 나타냄
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // UserDetails 인터페이스를 구현하여 Spring Security 에서 사용자 정보를 관리할 수 있도록 함

    @Id // 기본 키 필드를 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 데이터베이스에 위임
    private Long user_id;

    @Column(unique = true) // 필드를 unique 제약 조건으로 설정
    private String email;

    private String password;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    @ElementCollection(fetch = FetchType.EAGER) // roles 컬렉션을 별도의 테이블로 관리하고 즉시 로딩
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) // 외래 키 컬럼 이름 명시
    @Builder.Default
    private Set<String> roles = Set.of("ROLE_USER");

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    // 사용자의 id 반환 (고유값)
    @Override
    public String getUsername() {
        return email;
    }

    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
