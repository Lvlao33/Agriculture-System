package com.farmporject.backend.trade.service;

import com.farmporject.backend.trade.model.Demand;
import com.farmporject.backend.trade.repository.DemandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemandService {

    private final DemandRepository demandRepository;

    @Autowired
    public DemandService(DemandRepository demandRepository) {
        this.demandRepository = demandRepository;
    }

    public List<Demand> getAllDemands() {
        return demandRepository.findAll();
    }

    public List<Demand> getUserDemands(Long userId) {
        return demandRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Demand> searchDemands(String keyword) {
        return demandRepository.findByKeyword(keyword);
    }

    public List<Demand> getDemandsByCategory(String category) {
        return demandRepository.findByCategory(category);
    }

    public List<Demand> getDemandsByStatus(String status) {
        return demandRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public Optional<Demand> getDemandById(Long id) {
        return demandRepository.findById(id);
    }

    public Demand createDemand(Demand demand) {
        return demandRepository.save(demand);
    }

    public Demand updateDemand(Long id, Demand demandDetails) {
        return demandRepository.findById(id)
                .map(demand -> {
                    if (demandDetails.getTitle() != null) demand.setTitle(demandDetails.getTitle());
                    if (demandDetails.getDescription() != null) demand.setDescription(demandDetails.getDescription());
                    if (demandDetails.getCategory() != null) demand.setCategory(demandDetails.getCategory());
                    if (demandDetails.getQuantity() != null) demand.setQuantity(demandDetails.getQuantity());
                    if (demandDetails.getUnit() != null) demand.setUnit(demandDetails.getUnit());
                    if (demandDetails.getContactInfo() != null) demand.setContactInfo(demandDetails.getContactInfo());
                    if (demandDetails.getStatus() != null) demand.setStatus(demandDetails.getStatus());
                    return demandRepository.save(demand);
                })
                .orElseThrow(() -> new RuntimeException("Demand not found with id: " + id));
    }

    public void deleteDemand(Long id) {
        demandRepository.deleteById(id);
    }
}