package com.farmporject.backend.finance.service;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.repository.LoanRepository;

import com.farmporject.backend.finance.model.LoanFile;
import com.farmporject.backend.finance.repository.LoanFileRepository;
import java.io.File;
import java.nio.file.Path;

// 功能：贷款业务逻辑（申请处理、验证、保存、审批交互等）。
@Service
public class LoanService {

    private final LoanRepository repo;
    private final LoanFileRepository loanFileRepository;

    public LoanService(LoanRepository loanRepository, LoanFileRepository loanFileRepository) {
        this.repo = loanRepository;
        this.loanFileRepository = loanFileRepository;
    }

    /**
     * 提交贷款申请
     * 
     * @param loan 贷款申请信息
     * @return true 如果成功保存到数据库，false 如果保存失败
     */
    public boolean apply(Loan loan) {
        try {
            // 确保必要字段不为空
            if (loan == null || loan.getLoanAmount() == null || loan.getFarmerId() == null) {
                return false;
            }

            // 设置初始状态为已申请
            loan.setStatus(Loan.LoanStatus.APPLIED.name());
            // 设置申请时间
            loan.setApplicationDate(java.time.LocalDateTime.now());

            // 保存到数据库并确认是否成功
            Loan savedLoan = repo.save(loan);
            // 如果保存成功，savedLoan 会有 ID，返回 true
            return savedLoan.getId() != null;

        } catch (Exception e) {
            // 实际项目中应该使用日志记录异常
            // logger.error("Failed to save loan application", e);
            return false;
        }
    }

    /**
     * 提交融资证明资料文件
     * 
     * @param loanId 贷款ID
     * @return true 如果成功处理文件上传，false 如果失败
     */
    public boolean uploadFile(Long loanId, MultipartFile file) throws Exception {
        // 先查询贷款申请信息
        Loan loan = repo.findById(loanId).orElseThrow(() -> new Exception("贷款申请不存在"));

        // 上传文件并获取存储路径
        String filePath = saveFile(file);

        // 保存文件信息到数据库
        LoanFile loanFile = new LoanFile();
        loanFile.setLoan(loan);
        loanFile.setFileName(file.getOriginalFilename());
        loanFile.setFilePath(filePath);
        LoanFile temp = loanFileRepository.save(loanFile);
        if (temp.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");

        // 定义保存文件的目录（backend目录）
        String uploadDir = projectRoot + File.separator + "uploads";

        // 创建目录（如果目录不存在）
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs(); // 创建目录
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();

        // 定义文件保存路径
        Path path = Paths.get(uploadDir + File.separator + fileName);

        // 保存文件
        file.transferTo(path.toFile());

        // 返回保存文件的路径，供后续使用
        return path.toString();
    }

}
