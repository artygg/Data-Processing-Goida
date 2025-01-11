package com.example.Netflix.Prices;

import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.Subscriptions.Subscription;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Price {
    @Id
    private Integer id;
    private double price;
    @OneToMany
    private List<Resolution> resolutions;
    @OneToMany
    private List<Subscription> subscriptions;

    public Price() {

    }

    public Price(Integer id, double price, List<Resolution> resolutions) {
        this.id = id;
        this.price = price;
        this.resolutions = resolutions;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Resolution> getResolutions() {
        return this.resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    public List<Subscription> getSubscriptions() {
        return this.subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}