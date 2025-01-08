package com.example.Netflix.Prices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;

    public List<Price> findllPrices() {
        return priceRepository.findAll();
    }

    public void savePrice(Price price) {
        priceRepository.save(price);
    }

    public void deletePrice(int id) {
        priceRepository.deletePriceById(id);
    }
}
