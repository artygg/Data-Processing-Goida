package com.example.Netflix.Prices;

import com.example.Netflix.Generalization.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService extends BaseService<Price, Integer> {
    @Autowired
    private PriceRepository priceRepository;

    @Override
    protected JpaRepository<Price, Integer> getRepository() {
        return priceRepository;
    }
}
