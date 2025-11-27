package com.farmporject.backend.trade.controller;

import com.farmporject.backend.trade.model.Address;
import com.farmporject.backend.trade.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 交易模块-收发货地址
 * 路由前缀：/api/trade/addresses
 */
@RestController
@RequestMapping("/api/trade/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 地址列表
     * 示例：GET /api/trade/addresses?userId=1
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Address> addresses = addressService.getUserAddresses(userId);
            response.put("flag", true);
            response.put("data", addresses);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取地址列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 新增地址
     * 示例：POST /api/trade/addresses
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Address address) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证必要字段
            if (address.getUserId() == null) {
                response.put("flag", false);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getReceiverName() == null || address.getReceiverName().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "收货人姓名不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getReceiverPhone() == null || address.getReceiverPhone().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "收货人电话不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getProvince() == null || address.getProvince().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "省份不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getCity() == null || address.getCity().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "城市不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
                response.put("flag", false);
                response.put("message", "详细地址不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            Address createdAddress = addressService.createAddress(address);

            response.put("flag", true);
            response.put("message", "地址新增成功");
            response.put("data", createdAddress);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "新增地址失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 地址详情
     * 示例：GET /api/trade/addresses/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Address> address = addressService.getAddressById(id);
            if (address.isPresent()) {
                response.put("flag", true);
                response.put("data", address.get());
                return ResponseEntity.ok().body(response);
            } else {
                response.put("flag", false);
                response.put("message", "地址不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取地址详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新地址
     * 示例：PUT /api/trade/addresses/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Address address) {
        Map<String, Object> response = new HashMap<>();
        try {
            Address updatedAddress = addressService.updateAddress(id, address);
            response.put("flag", true);
            response.put("message", "地址更新成功");
            response.put("data", updatedAddress);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "更新地址失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除地址
     * 示例：DELETE /api/trade/addresses/1?userId=1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            addressService.deleteAddress(userId, id);
            response.put("flag", true);
            response.put("message", "地址删除成功");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除地址失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取默认地址
     * 示例：GET /api/trade/addresses/default?userId=1
     */
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultAddress(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Address> defaultAddress = addressService.getDefaultAddress(userId);
            if (defaultAddress.isPresent()) {
                response.put("flag", true);
                response.put("data", defaultAddress.get());
            } else {
                response.put("flag", true);
                response.put("data", null);
                response.put("message", "未设置默认地址");
            }
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取默认地址失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}