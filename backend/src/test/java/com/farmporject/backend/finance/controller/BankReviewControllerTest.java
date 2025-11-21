package com.farmporject.backend.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.model.Status;
import com.farmporject.backend.finance.service.LoanService;

@ExtendWith(MockitoExtension.class)
class BankReviewControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private BankReviewController controller;

    @Test
    public void listApps_returnsLoanListWithinLastMonth() {
        List<Loan> expectedLoans = List.of(new Loan(), new Loan());
        when(loanService.findLoanListByTime(any(), any())).thenReturn(expectedLoans);

        ResponseEntity<?> response = controller.listApps();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedLoans, response.getBody());

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(loanService).findLoanListByTime(startCaptor.capture(), endCaptor.capture());

        LocalDateTime start = startCaptor.getValue();
        LocalDateTime end = endCaptor.getValue();
        long daysBetween = Duration.between(start, end).toDays();

        assertTrue(start.isBefore(end));
        assertTrue(daysBetween >= 29 && daysBetween <= 31);
    }

    @Test
    public void approve_returnsOkWhenServiceSucceeds() {
        String id = "123";
        Long operatorId = 9L;
        when(loanService.submitByLoanId(123L, Status.APPROVED, operatorId, "approved by bank")).thenReturn(true);

        ResponseEntity<?> response = controller.approve(id, operatorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("approved 123", response.getBody());
        verify(loanService).submitByLoanId(123L, Status.APPROVED, operatorId, "approved by bank");
    }

    @Test
    public void approve_returnsBadRequestWhenServiceFails() {
        String id = "456";
        Long operatorId = 11L;
        when(loanService.submitByLoanId(456L, Status.APPROVED, operatorId, "approved by bank")).thenReturn(false);

        ResponseEntity<?> response = controller.approve(id, operatorId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("failed to approve 456", response.getBody());
        verify(loanService).submitByLoanId(456L, Status.APPROVED, operatorId, "approved by bank");
    }

    @Test
    public void reject_returnsOkWhenServiceSucceeds() {
        String id = "789";
        Long operatorId = 15L;
        when(loanService.submitByLoanId(789L, Status.REJECTED, operatorId, "rejected by bank")).thenReturn(true);

        ResponseEntity<?> response = controller.reject(id, operatorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rejected 789", response.getBody());
        verify(loanService).submitByLoanId(789L, Status.REJECTED, operatorId, "rejected by bank");
    }

    @Test
    public void reject_returnsBadRequestWhenServiceFails() {
        String id = "987";
        Long operatorId = 20L;
        when(loanService.submitByLoanId(987L, Status.REJECTED, operatorId, "rejected by bank")).thenReturn(false);

        ResponseEntity<?> response = controller.reject(id, operatorId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("failed to reject 987", response.getBody());
        verify(loanService).submitByLoanId(987L, Status.REJECTED, operatorId, "rejected by bank");
    }
}