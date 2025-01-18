package com.example.Netflix.Referals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    @Query(value = "SELECT referral_id_out AS id, host_id_out AS host_id, invited_id_out AS invited_id " +
            "FROM get_referral_by_invited_id(:invitedId);",
            nativeQuery = true)
    List<Referral> getReferralByInvitedId(@Param("invitedId") UUID invitedId);
}
