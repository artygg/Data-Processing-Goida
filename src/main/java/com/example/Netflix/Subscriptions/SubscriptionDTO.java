package com.example.Netflix.Subscriptions;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

public class SubscriptionDTO {
    @NotNull
    private UUID profile;

    @Column(name = "price_id", nullable = false)
    @NotNull
    private Integer priceId;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private Date startDate;

    @Column(name = "end_date")
    @NotNull
    private Date endDate;

    public SubscriptionDTO(UUID profile,
                           Integer priceId, Date startDate, Date endDate) {
        this.profile = profile;
        this.priceId = priceId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getProfile() {
        return this.profile;
    }

    public void setProfile(UUID profile) {
        this.profile = profile;
    }

    public Integer getPriceId() {
        return this.priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}