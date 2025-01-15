package com.example.Netflix.Referals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findReferralByInvitedId(UUID id);

    @Modifying
    @Transactional
    @Query(value = "CALL save_referral(:hostId, :invitedId)", nativeQuery = true)
    void saveReferral(
            @Param("hostId") UUID hostId,
            @Param("invitedId") UUID invitedId
    );

    @Query(value = "SELECT * \n" +
            "FROM get_referral_by_invited_id('931b01d7-c961-4b9d-867d-72128d54064b') AS t(referral_id_out, host_id_out, invited_id_out);", nativeQuery = true)
    Referral getReferralByInvitedId(@Param("invitedId") UUID invitedId);
}
