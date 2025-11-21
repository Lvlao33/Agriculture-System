package com.farmporject.backend.finance.service;

import com.farmporject.backend.finance.dto.LoanDTO;
import com.farmporject.backend.finance.model.*;
import com.farmporject.backend.finance.repository.*;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanFileService loanFileService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanProductRepository loanProductRepository;
    @Mock
    private LoanUserStatusRepository loanUserStatusRepository;
    @Mock
    private LoanRecordRepository loanRecordRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private User user;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setId(1L);
        user = new User();
        user.setId(100L);
    }

    @Test
    void apply() {
        LoanDTO dto = new LoanDTO();
        dto.setLoanAmount(BigDecimal.TEN);
        dto.setLoanPurpose("purpose");
        dto.setLoanTermMonths(12);
        dto.setInterestRate(BigDecimal.ONE);
        dto.setUserIds(List.of(50L));
        dto.setLoanProductId(77L);
        dto.setOperatorId(100L);

        User applicant = new User();
        applicant.setId(50L);
        LoanProduct product = new LoanProduct();
        product.setId(77L);

        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(userRepository.findById(50L)).thenReturn(Optional.of(applicant));
        when(loanProductRepository.findById(77L)).thenReturn(Optional.of(product));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan l = invocation.getArgument(0);
            if (l.getId() == null) {
                l.setId(1L);
            }
            return l;
        });
        when(loanUserStatusRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRecordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.apply(dto, dto.getOperatorId());

        assertNotNull(result);
        assertEquals(Status.CREATED, result.getStatus());
        verify(loanUserStatusRepository).save(any(LoanUserStatus.class));
        verify(loanRecordRepository).save(any(LoanRecord.class));

        assertNull(loanService.apply(null, 1L));
    }

    @Test
    void uploadFileByLoanId() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanFileService.uploadFile(loan, file, "ID")).thenReturn(true);

        assertTrue(loanService.uploadFileByLoanId(1L, file, "ID"));
        verify(loanFileService).uploadFile(loan, file, "ID");

        assertThrows(Exception.class, () -> loanService.uploadFileByLoanId(null, file, "ID"));
    }

    @Test
    void submitByLoanId() {
        Loan loanToUpdate = new Loan();
        loanToUpdate.setId(2L);
        when(loanRepository.findById(2L)).thenReturn(Optional.of(loanToUpdate));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.findById(100L)).thenReturn(Optional.of(user));

        assertTrue(loanService.submitByLoanId(2L, Status.APPROVED, 100L, "ok"));
        assertEquals(Status.APPROVED, loanToUpdate.getStatus());

        assertFalse(loanService.submitByLoanId(null, Status.APPROVED, 100L, "ok"));
    }

    @Test
    void findLoanListByTime() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(loanRepository.findByApplicationDateBetween(start, end)).thenReturn(List.of(loan));

        List<Loan> loans = loanService.findLoanListByTime(start, end);

        assertEquals(1, loans.size());
        verify(loanRepository).findByApplicationDateBetween(start, end);
    }

    @Test
    void update() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        assertTrue(loanService.update(loan));
        assertNotNull(loan.getUpdateDate());

        assertFalse(loanService.update(null));
    }
}
