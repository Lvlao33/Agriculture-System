package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Appointment;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.repository.AppointmentRepository;
import com.farmporject.backend.expert.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ExpertRepository expertRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, ExpertRepository expertRepository) {
        this.appointmentRepository = appointmentRepository;
        this.expertRepository = expertRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getUserAppointments(String userId) {
        return appointmentRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Appointment> getExpertAppointments(Long expertId) {
        return appointmentRepository.findByExpertIdOrderByCreateTimeDesc(expertId);
    }

    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByCreateTimeDesc(status);
    }

    public List<Appointment> getUserAppointmentsByStatus(String userId, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status);
    }

    public List<Appointment> getExpertAppointmentsByStatus(Long expertId, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByExpertIdAndStatusOrderByAppointmentTime(expertId, status);
    }

    public List<Appointment> getAppointmentsInTimeRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentTimeBetween(start, end);
    }

    public List<Appointment> getExpertAppointmentsInTimeRange(Long expertId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByExpertIdAndAppointmentTimeBetween(expertId, start, end);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        // 验证专家是否存在且可预约
        Expert expert = expertRepository.findById(appointment.getExpert().getId())
                .orElseThrow(() -> new RuntimeException("Expert not found with id: " + appointment.getExpert().getId()));

        if (!expert.getIsAvailable()) {
            throw new RuntimeException("Expert is not available for appointment");
        }

        // 验证预约时间是否在未来
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment time must be in the future");
        }

        // 设置默认状态
        if (appointment.getStatus() == null) {
            appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        }

        return appointmentRepository.save(appointment);
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
                    if (appointmentDetails.getAppointmentTime() != null) {
                        appointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
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

    public boolean hasConflictingAppointment(Long expertId, LocalDateTime appointmentTime) {
        LocalDateTime startTime = appointmentTime.minusHours(1);
        LocalDateTime endTime = appointmentTime.plusHours(1);

        List<Appointment> conflictingAppointments = appointmentRepository
                .findByExpertIdAndAppointmentTimeBetween(expertId, startTime, endTime);

        return !conflictingAppointments.isEmpty();
    }
}