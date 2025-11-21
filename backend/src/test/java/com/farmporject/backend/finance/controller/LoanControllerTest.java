package com.farmporject.backend.finance.controller;

import com.farmporject.backend.finance.dto.LoanDTO;
import com.farmporject.backend.finance.model.Loan;
import com.farmporject.backend.finance.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LoanController controller;

    @Test
    void apply() throws Exception {
        LoanDTO dto = new LoanDTO();
        dto.setOperatorId(9L);
        dto.setLoanProductId(2L);
        dto.setUserIds(List.of(1L));
        dto.setLoanAmount(BigDecimal.TEN);

        Loan loan = new Loan();
        loan.setId(88L);
        when(loanService.apply(dto, 9L)).thenReturn(loan);

        ResponseEntity<?> okResponse = controller.apply(dto, null, null, request);
        assertEquals(HttpStatus.OK, okResponse.getStatusCode());
        assertEquals("Loan application and file upload success", okResponse.getBody());

        reset(loanService);
        when(loanService.apply(dto, 9L)).thenReturn(null);
        ResponseEntity<?> failResponse = controller.apply(dto, null, null, request);
        assertEquals(HttpStatus.BAD_REQUEST, failResponse.getStatusCode());
        assertEquals("Loan application failed", failResponse.getBody());

        reset(loanService);
        when(loanService.apply(dto, 9L)).thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> errorResponse = controller.apply(dto, null, null, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
        assertEquals("Exception: boom", errorResponse.getBody());
    }

    @Test
    void list() {
        ResponseEntity<?> response = controller.list();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("loan list", response.getBody());
    }

    @Test
    void update() {
        Loan loan = new Loan();
        when(loanService.update(loan)).thenReturn(true);

        ResponseEntity<?> success = controller.update(loan);
        assertEquals(HttpStatus.OK, success.getStatusCode());
        assertEquals("update loan success", success.getBody());

        reset(loanService);
        when(loanService.update(loan)).thenReturn(false);
        ResponseEntity<?> fail = controller.update(loan);
        assertEquals(HttpStatus.BAD_REQUEST, fail.getStatusCode());
        assertEquals("update loan failed", fail.getBody());

        reset(loanService);
        when(loanService.update(loan)).thenThrow(new RuntimeException("err"));
        ResponseEntity<?> error = controller.update(loan);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
        assertEquals("update loan exception: err", error.getBody());
    }
}
