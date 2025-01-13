package com.example.Netflix.Subscriptions;

import com.example.Netflix.Profiles.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    @NotBlank(message = "Profile is required")
    private Profile profile;

    @Column(name = "price_id", nullable = false)
    @NotBlank(message = "Price id is required")
    private Integer priceId;

    @Column(name = "start_date", nullable = false)
    @NotBlank(message = "Start date required")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotBlank(message = "End date required")
    private LocalDate endDate;

    public Subscription() {
    }

    public Subscription(Profile profile, Integer priceId, LocalDate startDate, LocalDate endDate) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
