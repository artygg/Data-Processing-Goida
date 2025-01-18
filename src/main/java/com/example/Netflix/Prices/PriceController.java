package com.example.Netflix.Prices;

import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/prices")
public class PriceController extends BaseController<Price, Integer> {
    @Autowired
    private PriceService priceService;

    @Override
    protected BaseService<Price, Integer> getService() {
        return priceService;
    }
}
