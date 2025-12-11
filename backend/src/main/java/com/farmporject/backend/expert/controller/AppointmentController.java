package com.farmporject.backend.expert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.expert.dto.AppointmentDTO;
import com.farmporject.backend.expert.model.Appointment;
import com.farmporject.backend.expert.service.AppointmentService;

/**
 * 专家模块-预约
 * 路由前缀：/api/appointments
 * - GET /api/appointments 预约列表（我的/待处理）
 * - POST /api/appointments 创建预约
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * 预约列表
     * 示例：GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("appointments");
    }

    /**
     * 创建预约
     * 示例：POST /api/appointments/add
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Appointment>> create(@RequestBody AppointmentDTO appointmentDto) {
        try {
            Appointment appointment = appointmentService.createAppointmentByDTO(appointmentDto);
            if (appointment != null) {
                return ResponseEntity.status(201).body(ApiResponse.success(appointment));
            } else {
                return ResponseEntity.status(400).body(ApiResponse.fail("预约创建失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.fail(e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取预约列表
     * 示例：GET /api/appointments/{userId}/all
     */
    @GetMapping("/{userId}/all")
    public ResponseEntity<?> listByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(appointmentService.getUserAppointments(userId));
    }
}
