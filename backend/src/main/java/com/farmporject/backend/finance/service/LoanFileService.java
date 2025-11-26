package com.farmporject.backend.finance.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.LoanFile;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.finance.repository.LoanFileRepository;

import java.time.LocalDateTime;

@Service
public class LoanFileService {
    private final LoanFileRepository loanFileRepository;

    public LoanFileService(LoanFileRepository loanFileRepository) {
        this.loanFileRepository = loanFileRepository;
    }

    /**
     * 根据loanID获取该笔贷款资料文件列表
     * 
     * @param loanId 贷款ID
     * @return files 贷款资料文件列表
     * 
     */
    public List<LoanFile> getLoanFiles(Long loanId) {
        return loanFileRepository.findLoanFilesByLoanId(loanId);
    }

    /**
     * 提交融资证明资料文件
     * 
     * @param loanId   贷款ID
     * @param fileType 文件类型（例如：身份证、收入证明）
     * @return true 如果成功处理文件上传，false 如果失败
     */
    public boolean uploadFile(Loan loan, MultipartFile file, String fileType, User user) throws Exception {
        // 校验文件
        if (checkFile(file)) {
            // 上传文件并获取存储路径
            String filePath = saveFile(file, loan.getId(), fileType);

            // 保存文件信息到数据库
            LoanFile loanFile = new LoanFile();
            loanFile.setFileName(file.getOriginalFilename());
            loanFile.setFilePath(filePath);
            loanFile.setFileType(fileType); // 前端传入从哪个栏位传入
            loanFile.setCreatedAt(LocalDateTime.now());
            loanFile.setUpdatedAt(LocalDateTime.now());
            // loan.getLoanFiles().add(loanFile);
            loanFile.setLoan(loan);
            loanFile.setUser(user);
            LoanFile temp = loanFileRepository.save(loanFile);
            if (temp.getId() != null) {
                return true;
            } else {
                System.err.println();
                return false;
            }
        } else
            return false;
    }

    private String saveFile(MultipartFile file, Long loanId, String fileType) throws IOException {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");

        // 定义保存文件的目录（backend目录）
        // 路径结构：loans_files/{loanId}/{fileType}/
        String uploadDir = projectRoot + File.separator + "loans_files" + File.separator + loanId;
        if (fileType != null && !fileType.trim().isEmpty()) {
            uploadDir += File.separator + fileType;
        }

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
        File targetFile = Objects.requireNonNull(path.toFile(), "File path cannot be null");
        file.transferTo(targetFile);

        // 返回保存文件的路径，供后续使用
        return path.toString();
    }

    // 校验文件合法性
    private boolean checkFile(MultipartFile file) {
        // 校验文件类型
        if (!isValidFileType(file)) {
            return false;
        }

        // 校验文件名（防止路径遍历）
        if (containsInvalidCharacters(file.getOriginalFilename())) {
            return false;
        }

        return true;
    }

    // 校验文件类型（例如只允许上传图片或 PDF）
    private boolean isValidFileType(MultipartFile file) {
        String[] allowedTypes = { "image/png", "image/jpeg", "application/pdf" };
        String fileType = file.getContentType();
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    // 校验文件名，防止路径遍历攻击
    private boolean containsInvalidCharacters(String fileName) {
        return fileName.contains("..") || fileName.contains("/") || fileName.contains("\\");
    }

}
