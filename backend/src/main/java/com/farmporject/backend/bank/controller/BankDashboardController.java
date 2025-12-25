package com.farmporject.backend.bank.controller;

import com.farmporject.backend.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.Status;
import com.farmporject.backend.finance.repository.LoanIntentionRepository;
import com.farmporject.backend.finance.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bank/dashboard")
public class BankDashboardController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanIntentionRepository loanIntentionRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("pendingLoans", loanRepository.countByStatus(Status.REVIEWING));
        data.put("unassignedLoans", loanRepository.countByStatus(Status.CREATED));
        data.put("matchedFarmersIntention", loanIntentionRepository.count());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // 贷款审批队列 - REVIEWING 状态最老的三条
    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> loans() {
        List<Loan> list = loanRepository.findFirst3ByStatusOrderByApplicationDateAsc(Status.REVIEWING);
        List<Map<String, Object>> result = list.stream().map(loan -> {
            return buildLoan(
                    loan.getId().toString(),
                    loan.getApplicantName(),
                    loan.getLoanProduct() != null ? loan.getLoanProduct().getName() : "自定义产品",
                    loan.getLoanAmount().intValue(),
                    loan.getLoanTermMonths() + " months",
                    loan.getApplicationDate().toString(),
                    "urgent",
                    "Reviewing");
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 待分配结果 - CREATED 状态最老的三条
    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> matches() {
        List<Loan> list = loanRepository.findFirst3ByStatusOrderByApplicationDateAsc(Status.CREATED);
        List<Map<String, Object>> result = list.stream().map(loan -> {
            return buildLoan(
                    loan.getId().toString(),
                    loan.getApplicantName(),
                    loan.getLoanProduct() != null ? loan.getLoanProduct().getName() : "自定义产品",
                    loan.getLoanAmount().intValue(),
                    loan.getLoanTermMonths() + " months",
                    loan.getApplicationDate().toString(),
                    "urgent",
                    "Reviewing");
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 审批进度总览
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> stats() {
        long total = loanRepository.count();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Status status : Status.values()) {
            long count = loanRepository.countByStatus(status);
            Map<String, Object> map = new HashMap<>();
            map.put("title", getStatusLabel(status));
            map.put("value", count);
            map.put("percent", total == 0 ? 0 : (int) (count * 100 / total));
            map.put("color", getStatusColor(status));
            result.add(map);
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> notifications() {
        List<Map<String, String>> list = Arrays.asList(
                buildNotice("N001", "总部通知：11月农民贷款政策更新", "1 小时前"),
                buildNotice("N002", "系统维护：11/30 01:00-03:00 服务暂停", "今天"));
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    private Map<String, Object> buildLoan(String id, String farmer, String product,
            int amount, String term, String time, String level, String levelText) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("farmer", farmer);
        map.put("product", product);
        map.put("amount", amount);
        map.put("term", term);
        map.put("time", time);
        map.put("level", level);
        map.put("levelText", levelText);
        return map;
    }

    private Map<String, String> buildNotice(String id, String content, String time) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("time", time);
        return map;
    }

    private String getStatusLabel(Status status) {
        switch (status) {
            case CREATED:
                return "待分配";
            case REVIEWING:
                return "审核中";
            case APPROVED:
                return "待签约";
            case SIGNED:
                return "待放款";
            case REPAYING:
                return "还款中";
            case REJECTED:
                return "已拒绝";
            case CLEARED_NORMAL:
                return "已结清";
            case CLEARED_EARLY:
                return "提前结清";
            default:
                return status.name();
        }
    }

    private String getStatusColor(Status status) {
        switch (status) {
            case CREATED:
                return "#909399";
            case REVIEWING:
                return "#409EFF";
            case APPROVED:
                return "#E6A23C";
            case SIGNED:
                return "#F56C6C";
            case REPAYING:
                return "#67C23A";
            case REJECTED:
                return "#F56C6C";
            case CLEARED_NORMAL:
                return "#67C23A";
            case CLEARED_EARLY:
                return "#67C23A";
            default:
                return "#409EFF";
        }
    }
}
