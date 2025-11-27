package com.farmporject.backend.finance.service;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.LoanFile;
import com.farmporject.backend.finance.repository.LoanFileRepository;
import com.farmporject.backend.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanFileServiceTest {

    @Mock
    private LoanFileRepository loanFileRepository;

    @InjectMocks
    private LoanFileService loanFileService;

    private final AtomicReference<Path> createdFolder = new AtomicReference<>();
    
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(100L);
    }

    @AfterEach
    void cleanup() throws IOException {
        Path folder = createdFolder.getAndSet(null);
        if (folder != null && Files.exists(folder)) {
            Files.walk(folder)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void uploadFile() throws Exception {
        Loan loan = new Loan();
        loan.setId(1L);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "proof.png", "image/png", "content".getBytes());
        when(loanFileRepository.save(any(LoanFile.class))).thenAnswer(invocation -> {
            LoanFile file = invocation.getArgument(0);
            file.setId(10L);
            return file;
        });

        boolean result = loanFileService.uploadFile(loan, multipartFile, "ID", user);

        assertTrue(result);
        verify(loanFileRepository).save(any(LoanFile.class));
        Path expectedFolder = Path.of(System.getProperty("user.dir"), "loans_files", "1");
        assertTrue(Files.exists(expectedFolder));
        createdFolder.set(expectedFolder);
    }

    @Test
    void uploadFile_rejectsInvalidType() throws Exception {
        Loan loan = new Loan();
        loan.setId(2L);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "proof.txt", "text/plain", "content".getBytes());

        boolean result = loanFileService.uploadFile(loan, multipartFile, "ID", user);

        assertFalse(result);
        verify(loanFileRepository, never()).save(any());
    }

    @Test
    void uploadFile_rejectsInvalidFileName() throws Exception {
        Loan loan = new Loan();
        loan.setId(3L);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "../hack.png", "image/png", "content".getBytes());

        boolean result = loanFileService.uploadFile(loan, multipartFile, "ID", user);

        assertFalse(result);
        verify(loanFileRepository, never()).save(any());
    }
}
