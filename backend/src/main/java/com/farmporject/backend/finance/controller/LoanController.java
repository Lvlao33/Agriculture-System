package com.farmporject.backend.finance.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.farmporject.backend.finance.service.LoanService;
import com.farmporject.backend.finance.model.Loan;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

/** 融资-融资申请 */
@RestController
@RequestMapping("/api/finance/loans")
public class LoanController {

    private final LoanService loanService;
    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    // Spring 会自动注入 LoanService Bean
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // 填写融资申请
    // 必要的申请资料必须一次提交 资料可以后补
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 指定请求体为multipart/form-data类型
    public ResponseEntity<?> apply(@ModelAttribute Loan loan,
            @RequestPart(value = "file", required = false) MultipartFile[] files,
            HttpServletRequest request) {
        try {
            // debug: 如果 file 丢失，尝试列出 multipart 中的 part
            if (files == null || files.length == 0) {
                StringBuilder partsInfo = new StringBuilder();
                try {
                    if (request instanceof MultipartHttpServletRequest) {
                        MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) request;
                        partsInfo.append("Received multipart form keys: ");
                        mreq.getParameterMap().forEach((k, v) -> partsInfo.append(k).append(", "));
                        partsInfo.append("; received file keys: ");
                        mreq.getFileNames().forEachRemaining(name -> partsInfo.append(name).append(", "));
                    } else if (request.getParts() != null) {
                        partsInfo.append("Parts: ");
                        request.getParts().forEach(p -> partsInfo.append(p.getName()).append(", "));
                    } else {
                        partsInfo.append("No multipart parts available from request object.");
                    }
                } catch (Exception ex) {
                    partsInfo.append("(failed to enumerate parts: ").append(ex.getMessage()).append(")");
                }
                logger.warn("File part missing in request. {}", partsInfo.toString());
                return ResponseEntity.badRequest().body("Required part 'file' is not present. " + partsInfo.toString());
            }
            // 1. 先处理贷款申请
            boolean isLoanApplied = loanService.apply(loan);
            if (!isLoanApplied) {
                return ResponseEntity.status(400).body("Loan application failed");
            }

            // 2. 然后上传融资证明文件（支持多个文件）
            boolean anyUploadFailed = false;
            for (MultipartFile mf : files) {
                if (mf == null || mf.isEmpty())
                    continue;
                boolean ok = loanService.uploadFileByLoanId(loan.getId(), mf);
                if (!ok)
                    anyUploadFailed = true;
            }
            if (anyUploadFailed) {
                return ResponseEntity.status(400).body("One or more file uploads failed");
            }

            // 3. 成功
            return ResponseEntity.ok().body("Loan application and file upload success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Exception: " + e.getMessage());
        }
    }

    // // 提交融资证明资料文件
    // @PostMapping("/{loan_id}/upload")
    // public ResponseEntity<?> uploadFile(@PathVariable("loan_id") Long loanId,
    // @RequestParam("file") MultipartFile file) {
    // // 调用服务层完成提交融资证明资料
    // try {
    // if (loanService.uploadFileByLoanId(loanId, file)) {
    // return ResponseEntity.ok().body("upload file success");
    // } else {
    // return ResponseEntity.status(400).body("upload file failed");
    // }
    // } catch (Exception e) {
    // return ResponseEntity.status(500).body("upload file exception: " +
    // e.getMessage());
    // }
    // }

    // 融资申请列表
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body("loan list");
    }
}
