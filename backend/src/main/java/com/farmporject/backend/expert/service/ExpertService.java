package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpertService {

    private final ExpertRepository expertRepository;

    @Autowired
    public ExpertService(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }

    public List<Expert> getAvailableExperts() {
        return expertRepository.findByIsAvailableTrue();
    }

    public Optional<Expert> getExpertById(Long id) {
        return expertRepository.findById(id);
    }

    public Optional<Expert> getExpertByUserId(Long userId) {
        return expertRepository.findByUser_Id(userId);
    }

    public List<Expert> searchExpertsBySpecialty(String specialty) {
        return expertRepository.findBySpecialtiesContaining(specialty);
    }

    public List<Expert> searchExpertsByKeyword(String keyword) {
        return expertRepository.findByKeyword(keyword);
    }

    public List<Expert> searchExpertsByName(String name) {
        return expertRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Expert> searchExpertsByTitle(String title) {
        return expertRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Expert> getExpertsByExperience(Integer minYears) {
        return expertRepository.findByExperienceYearsGreaterThanEqual(minYears);
    }

    public Expert createExpert(Expert expert) {
        return expertRepository.save(expert);
    }

    public Expert updateExpert(Long id, Expert expertDetails) {
        return expertRepository.findById(id)
                .map(expert -> {
                    if (expertDetails.getName() != null) {
                        expert.setName(expertDetails.getName());
                    }
                    if (expertDetails.getTitle() != null) {
                        expert.setTitle(expertDetails.getTitle());
                    }
                    if (expertDetails.getAvatar() != null) {
                        expert.setAvatar(expertDetails.getAvatar());
                    }
                    if (expertDetails.getDescription() != null) {
                        expert.setDescription(expertDetails.getDescription());
                    }
                    if (expertDetails.getSpecialties() != null) {
                        expert.setSpecialties(expertDetails.getSpecialties());
                    }
                    if (expertDetails.getExperienceYears() != null) {
                        expert.setExperienceYears(expertDetails.getExperienceYears());
                    }
                    if (expertDetails.getContactInfo() != null) {
                        expert.setContactInfo(expertDetails.getContactInfo());
                    }
                    if (expertDetails.getIsAvailable() != null) {
                        expert.setIsAvailable(expertDetails.getIsAvailable());
                    }
                    return expertRepository.save(expert);
                })
                .orElseThrow(() -> new RuntimeException("Expert not found with id: " + id));
    }

    public Expert updateExpertAvailability(Long id, Boolean isAvailable) {
        return expertRepository.findById(id)
                .map(expert -> {
                    expert.setIsAvailable(isAvailable);
                    return expertRepository.save(expert);
                })
                .orElseThrow(() -> new RuntimeException("Expert not found with id: " + id));
    }

    public void deleteExpert(Long id) {
        if (expertRepository.existsById(id)) {
            expertRepository.deleteById(id);
        } else {
            throw new RuntimeException("Expert not found with id: " + id);
        }
    }

    public boolean existsById(Long id) {
        return expertRepository.existsById(id);
    }

    public long countAllExperts() {
        return expertRepository.count();
    }

    public long countAvailableExperts() {
        return expertRepository.findByIsAvailableTrue().size();
    }
}