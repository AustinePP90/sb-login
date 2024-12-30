package com.mysite.login.repository;

import com.mysite.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // JpaRepository<User, Long> 인터페이스를 상속받아 기본적이 CRUD 기능을 제공받음
    Optional<User> findByEmail(String email); // email 로 사용자 정보를 조회할 수 있도록 함
}
