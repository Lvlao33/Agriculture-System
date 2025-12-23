package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.dto.ExpertDTO;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.repository.ExpertRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpertService {

    private final ExpertRepository expertRepository;

    private ExpertDTO tranExpertDTO(Expert expert) {
        if (expert == null) {
            return null; // 如果传入的专家实体为空，返回null
        }

        // 强制初始�? specialties 集合
        Hibernate.initialize(expert.getSpecialties());

        ExpertDTO dto = new ExpertDTO();
        dto.setId(expert.getId());
        if (expert.getUser() != null) {
            dto.setUserId(expert.getUser().getId());
            dto.setUsername(expert.getUser().getUsername());
            dto.setAvatar(expert.getUser().getAvatar());
        } else {
            dto.setAvatar(expert.getAvatar());
        }
        dto.setName(expert.getName());
        dto.setTitle(expert.getTitle());
        dto.setDescription(expert.getDescription());
        dto.setSpecialties(expert.getSpecialties());
        dto.setExperienceYears(expert.getExperienceYears());
        dto.setContactInfo(expert.getContactInfo());
        dto.setIsAvailable(expert.getIsAvailable());
        dto.setCreateTime(expert.getCreateTime());
        dto.setUpdateTime(expert.getUpdateTime());
        return dto;
    }

    @Autowired
    public ExpertService(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    @Transactional
    public List<ExpertDTO> getAllExperts() {
        List<Expert> experts = expertRepository.findAll();
        return experts.stream().map(this::tranExpertDTO).toList();
    }

    public List<Expert> getAvailableExperts() {
        return expertRepository.findByIsAvailableTrue();
    }

    public Optional<Expert> getExpertById(Long id) {
        return expertRepository.findById(id);
    }

    public Optional<Expert> getExpertByUserId(Long userId) {
        List<Expert> experts = expertRepository.findByUser_Id(userId);
        return experts.isEmpty() ? Optional.empty() : Optional.of(experts.get(0));
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