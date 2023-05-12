package com.dxvalley.nedajpaymnetbackend.security.repository;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
//    Optional<User> findByClientKey(String clientKey);
}
