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

        // 强制初始化 specialties 集合
        Hibernate.initialize(expert.getSpecialties());

        ExpertDTO expertDTO = new ExpertDTO();
        expertDTO.setId(expert.getId());
        expertDTO.setUserId(expert.getUser().getId());
        expertDTO.setName(expert.getName());
        expertDTO.setTitle(expert.getTitle());
        expertDTO.setAvatar(expert.getUser().getAvatar());
        expertDTO.setDescription(expert.getDescription());
        expertDTO.setSpecialties(expert.getSpecialties());
        expertDTO.setExperienceYears(expert.getExperienceYears());
        expertDTO.setContactInfo(expert.getContactInfo());
        expertDTO.setIsAvailable(expert.getIsAvailable());
        expertDTO.setCreateTime(expert.getCreateTime());
        expertDTO.setUpdateTime(expert.getUpdateTime());
        return expertDTO;
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