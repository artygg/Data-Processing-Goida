package com.example.Netflix.Resolutions;

import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resolutions")
public class ResolutionController extends BaseController<Resolution, Integer> {
    @Autowired
    private ResolutionService resolutionService;

    @Override
    protected BaseService<Resolution, Integer> getService() {
        return resolutionService;
    }
}