package com.example.Netflix.ApiUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {
    Optional<ApiUser> findSystemUserById(Long id);
    Optional<ApiUser> findSystemUserByLogin(String login);
}
