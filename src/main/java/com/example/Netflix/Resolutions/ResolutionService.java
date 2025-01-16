package com.example.Netflix.Resolutions;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResolutionService {
    @Autowired
    private ResolutionRepository resolutionRepository;

    @Transactional
    public void saveResolution(Resolution resolution) {
        resolutionRepository.save(resolution);
    }
    public Optional<Resolution> findResolutionById(int id) {
        return resolutionRepository.findResolutionById(id);
    }

    public List<Resolution> findAllResolutions() {
        return resolutionRepository.findAll();
    }

    @Transactional
    public void deleteResolution(int id) {
        resolutionRepository.deleteById(id);
    }
}
