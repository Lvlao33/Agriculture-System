package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Appointment> findByExpertIdOrderByCreateTimeDesc(Long expertId);

    List<Appointment> findByExpertIdAndStatusOrderByAppointmentTime(Long expertId,
            Appointment.AppointmentStatus status);

    List<Appointment> findByStatusOrderByCreateTimeDesc(Appointment.AppointmentStatus status);

    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.expert.id = :expertId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByExpertIdAndAppointmentTimeBetween(Long expertId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, Appointment.AppointmentStatus status);

    long countByExpertIdAndStatus(Long expertId, Appointment.AppointmentStatus status);
}