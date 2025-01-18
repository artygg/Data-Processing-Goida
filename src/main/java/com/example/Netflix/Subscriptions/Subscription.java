package com.example.Netflix.Subscriptions;

import com.example.Netflix.Profiles.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    @NotNull(message = "Profile is required")
    private Profile profile;

    @Column(name = "price_id", nullable = false)
    @NotNull(message = "Price id is required")
    private Integer priceId;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private Date startDate;

    @Column(name = "end_date")
    @NotNull(message = "End date is required")
    private Date endDate;

    public Subscription() {
    }

    public Subscription(Profile profile, Integer priceId, Date startDate, Date endDate) {
        this.profile = profile;
        this.priceId = priceId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
