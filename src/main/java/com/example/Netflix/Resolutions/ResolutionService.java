package com.example.Netflix.Resolutions;

import com.example.Netflix.Generalization.BaseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResolutionService extends BaseService<Resolution, Integer> {
    @Autowired
    private ResolutionRepository resolutionRepository;

    @Override
    protected JpaRepository<Resolution, Integer> getRepository() {
        return resolutionRepository;
    }
}
