package com.farmporject.backend.finance.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.dto.LoanDTO;
import com.farmporject.backend.finance.dto.JointApplicantDTO;
import com.farmporject.backend.finance.model.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {

    private final LoanService loanService;

    // Spring 会自动注入 LoanService Bean
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // 填写融资申请
    // 必要的申请资料必须一次提交 资料可以后补
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 指定请求体为multipart/form-data类型
    public ResponseEntity<?> apply(@ModelAttribute LoanDTO loanDTO,
            @RequestPart(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "fileTypes", required = false) java.util.List<String> fileTypes,
            HttpServletRequest request) {
        try {
            // 1. 先处理贷款申请
            Loan appliedLoan = loanService.apply(loanDTO, loanDTO.getOperatorId());
            if (appliedLoan == null) {
                return ResponseEntity.status(400).body("Loan application failed");
            }

            // 2. 然后上传融资证明文件（支持多个文件）
            boolean anyUploadFailed = false;
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    MultipartFile mf = files[i];
                    if (mf == null || mf.isEmpty())
                        continue;

                    String fileType = null;
                    if (fileTypes != null && i < fileTypes.size()) {
                        fileType = fileTypes.get(i);
                    }

                    boolean ok = loanService.uploadFileByLoanId(appliedLoan.getId(), mf, fileType,
                            loanDTO.getOperatorId());
                    if (!ok)
                        anyUploadFailed = true;
                }
            }
            if (anyUploadFailed) {
                return ResponseEntity.status(400).body("One or more file uploads failed");
            }

            // 3. 成功
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Loan application and file upload success");
            response.put("loanId", appliedLoan.getId());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Exception: " + e.getMessage());
        }
    }

    // 根据userid获取融资申请列表
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "userId", required = false) Long userId) {
        try {
            List<Loan> loans = loanService.findLoanListByUserId(userId);
            return ResponseEntity.ok().body(loans);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("loan list exception: " + e.getMessage());
        }
    }

    // 修改融资申请详情
    @PostMapping("/{loan_id}/update")
    public ResponseEntity<?> update(@PathVariable("loan_id") Long loanId, @RequestBody LoanDTO loanDto) {
        try {
            if (loanId == null) {
                return ResponseEntity.status(400).body("loan_id is required");
            }
            if (loanService.update(loanId, loanDto)) {
                return ResponseEntity.ok().body("update loan success");
            } else {
                return ResponseEntity.status(400).body("update loan failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("update loan exception: " + e.getMessage());
        }
    }

    /* LoanFile 相关接口 */
    // 根据贷款ID获得融资文件资料
    @GetMapping("/{loan_id}/files")
    public ResponseEntity<?> files(@PathVariable("loan_id") Long loanId) {
        try {
            List<LoanFile> loanFiles = loanService.findLoanFileList(loanId);
            return ResponseEntity.ok().body(loanFiles);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("loan file list exception: " + e.getMessage());
        }
    }

    // 修改融资文件资料
    @PostMapping("/{loan_id}/upload")
    public ResponseEntity<?> upload(@PathVariable("loan_id") Long loanId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "operatorId", required = false) Long operatorId) {
        try {
            if (loanId == null) {
                return ResponseEntity.status(400).body("loan_id is required");
            }
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(400).body("file is required");
            }
            if (loanService.uploadFileByLoanId(loanId, file, fileType, operatorId)) {
                return ResponseEntity.ok().body("upload file success");
            } else {
                return ResponseEntity.status(400).body("upload file failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("upload file exception: " + e.getMessage());
        }
    }

    /* LoanUserStatus 相关接口 */
    // 根据LoanID查询LoanUserStatus列表
    @GetMapping("/{loan_id}/user-statuses")
    public ResponseEntity<?> userStatuses(@PathVariable("loan_id") Long loanId) {
        try {
            List<LoanUserStatus> loanUserStatuses = loanService.findLoanUserStatusList(loanId);
            return ResponseEntity.ok().body(loanUserStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("loan user status list exception: " + e.getMessage());
        }
    }

    /* LoanRecord 相关接口 */
    // 根据LoanID查询LoanRecord列表
    @GetMapping("/{loan_id}/records")
    public ResponseEntity<?> records(@PathVariable("loan_id") Long loanId) {
        try {
            List<LoanRecord> loanRecords = loanService.findLoanRecordList(loanId);
            return ResponseEntity.ok().body(loanRecords);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("loan record list exception: " + e.getMessage());
        }
    }

    // 更新联合贷款人信息
    @PostMapping("/applicant/info")
    public ResponseEntity<?> updateApplicantInfo(@RequestBody JointApplicantDTO applicantDTO) {
        try {
            if (loanService.updateApplicantInfo(applicantDTO)) {
                return ResponseEntity.ok().body("update applicant info success");
            } else {
                return ResponseEntity.status(400).body("update applicant info failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("update applicant info exception: " + e.getMessage());
        }
    }

    // 用户签署贷款
    @PostMapping("/{loan_id}/user-sign")
    public ResponseEntity<?> userSign(@PathVariable("loan_id") Long loanId, @RequestParam Long userId) {
        try {
            if (loanService.userSignLoan(loanId, userId)) {
                return ResponseEntity.ok().body("sign success");
            } else {
                return ResponseEntity.status(400).body("sign failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("sign exception: " + e.getMessage());
        }
    }
}
