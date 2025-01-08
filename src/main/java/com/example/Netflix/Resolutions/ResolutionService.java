package com.example.Netflix.Resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResolutionService {
    @Autowired
    private ResolutionRepository resolutionRepository;

    public void saveResolution(Resolution resolution) {
        resolutionRepository.save(resolution);
    }

    public Optional<Resolution> findResolutionById(int id) {
        return resolutionRepository.findResolutionById(id);
    }

    public List<Resolution> findAllResolutions() {
        return resolutionRepository.findAll();
    }

    public void deleteResolution(int id) {
        resolutionRepository.deleteResolutionById(id);
    }
}
