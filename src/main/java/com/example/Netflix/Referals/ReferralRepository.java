package com.example.Netflix.Referals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findReferralByInvitedId(UUID id);

    @Transactional
    @Query(value = "CALL save_referral(:hostId, :invitedId)", nativeQuery = true)
    void saveReferral(
            @Param("hostId") UUID hostId,
            @Param("invitedId") UUID invitedId
    );

    @Transactional
    @Query(value = "SELECT * FROM get_referral_by_invited_id(:invitedId)", nativeQuery = true)
    Referral getReferralByInvitedId(@Param("invitedId") UUID invitedId);
}
