package com.example.Netflix.Subtitle;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtitleService {
    private final SubtitleRepository subtitleRepository;

    public SubtitleService(SubtitleRepository subtitleRepository) {
        this.subtitleRepository = subtitleRepository;
    }

    public List<Subtitle> getSubtitlesByContentId(Long contentId) {
        return subtitleRepository.findAllByContentId(contentId);
    }

    public Subtitle createSubtitle(Subtitle subtitle) {
        return subtitleRepository.save(subtitle);
    }

    public void deleteSubtitle(Long contentId, String language) {
        subtitleRepository.deleteById(new SubtitleId(contentId, language));
    }
}