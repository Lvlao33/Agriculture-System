package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.model.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 银行端-融资审批 */
@RestController
@RequestMapping("/api/finance/bank-review")
public class BankReviewController {
    private final LoanService loanService;

    public BankReviewController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/applications")
    public ResponseEntity<?> listApps() {
        // 默认查距离现在30天内的申请
        return ResponseEntity.ok()
                .body(loanService.findLoanListByTime(LocalDateTime.now().minusDays(30), LocalDateTime.now()));
    }

    /**
     * 查询未分配的贷款（CREATED状态且staff为null）
     */
    @GetMapping("/loans/created")
    public ResponseEntity<?> getCreatedLoans() {
        try {
            return ResponseEntity.ok().body(loanService.findUnassignedLoans());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据状态和负责人员查询贷款列表
     */
    @GetMapping("/loans/by-status")
    public ResponseEntity<?> getLoansByStatus(@RequestParam String status, @RequestParam Long staffId) {
        try {
            Status loanStatus = Status.valueOf(status);
            return ResponseEntity.ok().body(loanService.findLoanListByStatusAndStaff(loanStatus, staffId));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "无效的状态: " + status);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据多个状态和负责人员查询贷款列表（用于查询已结束的贷款）
     */
    @GetMapping("/loans/by-statuses")
    public ResponseEntity<?> getLoansByStatuses(@RequestParam List<String> statuses, @RequestParam Long staffId) {
        try {
            List<Status> statusList = new ArrayList<>();
            for (String statusStr : statuses) {
                statusList.add(Status.valueOf(statusStr));
            }
            return ResponseEntity.ok().body(loanService.findLoanListByStatusesAndStaff(statusList, staffId));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "无效的状态: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 分配人员处理贷款申请
     */
    @PostMapping("/applications/{id}/assign")
    public ResponseEntity<?> assign(@PathVariable String id, @RequestParam Long operatorId) {
        try {
            if (loanService.assign(Long.parseLong(id), operatorId)) {
                // 分配后，将状态改为REVIEWING
                loanService.submitByLoanId(Long.parseLong(id), Status.REVIEWING, operatorId, "已分配负责人，开始审核");
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "分配成功");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "分配失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "分配失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 审核通过
     */
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam(required = false) String remark) {
        try {
            String remarkText = remark != null ? remark : "审核通过";
            if (loanService.submitByLoanId(Long.parseLong(id), Status.APPROVED, operatorId, remarkText)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "审核通过");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "审核失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "审核失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam(required = false) String remark) {
        try {
            String remarkText = remark != null ? remark : "审核不通过";
            if (loanService.submitByLoanId(Long.parseLong(id), Status.REJECTED, operatorId, remarkText)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "已拒绝");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "拒绝失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "拒绝失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批注（不改变状态，只添加备注）
     */
    @PostMapping("/applications/{id}/comment")
    public ResponseEntity<?> comment(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam String remark) {
        try {
            // 直接调用submitByLoanId，传入当前状态，这样不会改变状态，只会更新备注和创建记录
            // 但需要先获取当前loan的状态
            com.farmporject.backend.finance.model.Loan loan = loanService.findLoanListByStatusAndStaff(
                    com.farmporject.backend.finance.model.Status.REVIEWING, operatorId).stream()
                    .filter(l -> l.getId().equals(Long.parseLong(id)))
                    .findFirst()
                    .orElse(null);

            if (loan == null) {
                // 尝试其他状态
                for (Status status : new Status[] { Status.APPROVED, Status.SIGNED, Status.REPAYING }) {
                    loan = loanService.findLoanListByStatusAndStaff(status, operatorId).stream()
                            .filter(l -> l.getId().equals(Long.parseLong(id)))
                            .findFirst()
                            .orElse(null);
                    if (loan != null)
                        break;
                }
            }

            if (loan == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "贷款不存在或无权操作");
                return ResponseEntity.badRequest().body(response);
            }

            // 使用当前状态调用submitByLoanId，这样状态不会改变，只会更新备注和创建记录
            if (loanService.submitByLoanId(Long.parseLong(id), loan.getStatus(), operatorId, "批注: " + remark)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "批注成功");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "批注失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "批注失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 签约（将状态从APPROVED改为SIGNED）
     */
    @PostMapping("/applications/{id}/sign")
    public ResponseEntity<?> sign(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam(required = false) String remark) {
        try {
            String remarkText = remark != null ? remark : "已签约";
            if (loanService.submitByLoanId(Long.parseLong(id), Status.SIGNED, operatorId, remarkText)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "签约成功");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "签约失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "签约失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 放款（将状态从SIGNED改为REPAYING）
     */
    @PostMapping("/applications/{id}/disburse")
    public ResponseEntity<?> disburse(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam(required = false) String remark) {
        try {
            String remarkText = remark != null ? remark : "已放款";
            if (loanService.submitByLoanId(Long.parseLong(id), Status.REPAYING, operatorId, remarkText)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "放款成功");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "放款失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "放款失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 还清（将状态从REPAYING改为CLEARED_NORMAL）
     */
    @PostMapping("/applications/{id}/clear")
    public ResponseEntity<?> clear(@PathVariable String id, @RequestParam Long operatorId,
            @RequestParam(required = false) String remark) {
        try {
            String remarkText = remark != null ? remark : "已还清";
            if (loanService.submitByLoanId(Long.parseLong(id), Status.CLEARED_NORMAL, operatorId, remarkText)) {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", true);
                response.put("message", "还清成功");
                return ResponseEntity.ok().body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("flag", false);
                response.put("message", "还清失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "还清失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
