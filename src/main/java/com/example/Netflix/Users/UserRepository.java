package com.example.Netflix.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(UUID id);

    @Transactional
    @Modifying
    @Query(value = "CALL update_user_credentials(:userId, :newEmail, :newPassword)", nativeQuery = true)
    void updateUserCredentials(@Param("userId") UUID userId,
                               @Param("newEmail") String newEmail,
                               @Param("newPassword") String newPassword);

    @Transactional
    @Modifying
    @Query(value = "CALL create_referral(:hostUserId, :invitedUserId)", nativeQuery = true)
    void createReferral(@Param("hostUserId") UUID hostUserId, @Param("invitedUserId") UUID invitedUserId);
}
