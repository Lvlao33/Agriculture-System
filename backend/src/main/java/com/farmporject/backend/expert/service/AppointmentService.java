package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Appointment;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.repository.AppointmentRepository;
import com.farmporject.backend.expert.repository.ExpertRepository;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import com.farmporject.backend.expert.dto.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, ExpertRepository expertRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.expertRepository = expertRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Transactional
    public List<AppointmentDTO> getUserAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository.findByUserIdOrderByCreateTimeDesc(userId);

        // 转换为DTO
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(appointment -> new AppointmentDTO(
                        appointment.getUserName(), appointment.getUserContact(), appointment.getAppointmentStartTime(),
                        appointment.getAppointmentEndTime(),
                        appointment.getDescription(),
                        appointment.getExpert().getId(), appointment.getUser().getId(),
                        appointment.getStatus().toString(),
                        appointment.getCreateTime(), appointment.getUpdateTime(), appointment.getExpert().getName(),
                        appointment.getId()))
                .collect(Collectors.toList());

        return appointmentDTOs;
    }

    public List<Appointment> getExpertAppointments(Long expertId) {
        return appointmentRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
    }

    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Appointment> getUserAppointmentsByStatus(Long userId, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status);
    }

    // public List<Appointment> getExpertAppointmentsByStatus(Long expertId,
    // Appointment.AppointmentStatus status) {
    // return
    // appointmentRepository.findByExpertIdAndStatusOrderByAppointmentTime(expertId,
    // status);
    // }

    // public List<Appointment> getAppointmentsInTimeRange(LocalDateTime start,
    // LocalDateTime end) {
    // return appointmentRepository.findByAppointmentTimeBetween(start, end);
    // }

    // public List<Appointment> getExpertAppointmentsInTimeRange(Long expertId,
    // LocalDateTime start, LocalDateTime end) {
    // return
    // appointmentRepository.findByExpertIdAndAppointmentTimeBetween(expertId,
    // start, end);
    // }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointmentByDTO(AppointmentDTO appointmentDTO) {
        // 验证用户是否存在
        // 可能只传入了username而没有传入userID
        User user = null;
        if (appointmentDTO.getUserId() == null && appointmentDTO.getUserName() != null) {
            List<User> users = userRepository.findByUsername(appointmentDTO.getUserName()).stream()
                    .collect(Collectors.toList());
            if (users.size() == 0) {
                throw new RuntimeException("User not found with username: " + appointmentDTO.getUserName());
            }
            user = users.get(0);
        } else if (appointmentDTO.getUserId() != null && appointmentDTO.getUserName() == null) {
            user = userRepository.findById(appointmentDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + appointmentDTO.getUserId()));
        } else {
            throw new RuntimeException("User not found with id: " + appointmentDTO.getUserId());
        }

        // 验证专家是否存在且可预约
        Expert expert = null;
        if (appointmentDTO.getExpertId() != null) {
            expert = expertRepository.findById(appointmentDTO.getExpertId())
                    .orElseThrow(
                            () -> new RuntimeException("Expert not found with id: " + appointmentDTO.getExpertId()));
            if (!expert.getIsAvailable()) {
                throw new RuntimeException("Expert is not available for appointment");
            }
        }

        // 验证预约时间是否在未来
        if (appointmentDTO.getStartTime() == null || appointmentDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment time must be in the future");
        }

        // 根据DTO创建Appointment实体
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setUserName(user.getUsername());
        appointment.setExpert(expert);
        appointment.setAppointmentStartTime(appointmentDTO.getStartTime());
        appointment.setAppointmentEndTime(appointmentDTO.getEndTime());
        appointment.setDescription(appointmentDTO.getDescription());
        appointment.setUserContact(appointmentDTO.getUserContact());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    /**
     * 将 Appointment 实体转换为 AppointmentDTO
     * 避免懒加载异常
     */
    public AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setUserName(appointment.getUserName());
        dto.setUserContact(appointment.getUserContact());
        dto.setStartTime(appointment.getAppointmentStartTime());
        dto.setEndTime(appointment.getAppointmentEndTime());
        dto.setDescription(appointment.getDescription());
        dto.setStatus(appointment.getStatus() != null ? appointment.getStatus().toString() : "PENDING");
        dto.setCreateTime(appointment.getCreateTime());
        dto.setUpdateTime(appointment.getUpdateTime());

        // 安全地访问懒加载字段
        try {
            if (appointment.getUser() != null) {
                dto.setUserId(appointment.getUser().getId());
            }
        } catch (Exception e) {
            // 懒加载失败,使用默认值
            dto.setUserId(null);
        }

        try {
            if (appointment.getExpert() != null) {
                dto.setExpertId(appointment.getExpert().getId());
                dto.setExpertName(appointment.getExpert().getName());
            }
        } catch (Exception e) {
            // 懒加载失败,使用默认值
            dto.setExpertId(null);
            dto.setExpertName(null);
        }

        return dto;
    }

    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setStatus(status);
                    return appointmentRepository.save(appointment);
                })
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    if (appointmentDetails.getAppointmentStartTime() != null) {
                        appointment.setAppointmentStartTime(appointmentDetails.getAppointmentStartTime());
                    }
                    if (appointmentDetails.getAppointmentEndTime() != null) {
                        appointment.setAppointmentEndTime(appointmentDetails.getAppointmentEndTime());
                    }
                    if (appointmentDetails.getDescription() != null) {
                        appointment.setDescription(appointmentDetails.getDescription());
                    }
                    if (appointmentDetails.getUserContact() != null) {
                        appointment.setUserContact(appointmentDetails.getUserContact());
                    }
                    if (appointmentDetails.getStatus() != null) {
                        appointment.setStatus(appointmentDetails.getStatus());
                    }
                    return appointmentRepository.save(appointment);
                })
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    public void deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
    }

    public long countAppointmentsByExpertAndStatus(Long expertId, Appointment.AppointmentStatus status) {
        return appointmentRepository.countByExpertIdAndStatus(expertId, status);
    }

    // public boolean hasConflictingAppointment(Long expertId, LocalDateTime
    // appointmentTime) {
    // LocalDateTime startTime = appointmentTime.minusHours(1);
    // LocalDateTime endTime = appointmentTime.plusHours(1);

    // List<Appointment> conflictingAppointments = appointmentRepository
    // .findByExpertIdAndAppointmentTimeBetween(expertId, startTime, endTime);

    // return !conflictingAppointments.isEmpty();
    // }
}