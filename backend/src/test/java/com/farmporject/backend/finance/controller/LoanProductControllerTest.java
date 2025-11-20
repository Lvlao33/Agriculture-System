package com.farmporject.backend.finance.controller;

import com.farmporject.backend.finance.model.LoanProduct;
import com.farmporject.backend.finance.service.LoanProductService;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanProductControllerTest {

    @Mock
    private LoanProductService loanProductService;

    @InjectMocks
    private LoanProductController controller;

    @Test
    void list() {
        LoanProduct product = new LoanProduct();
        product.setId(1L);
        product.setName("product");
        product.setAmount(BigDecimal.TEN);
        when(loanProductService.getProductsForFarmer()).thenReturn(List.of(product));

        ResponseEntity<List<LoanProduct>> response = controller.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(product, response.getBody().get(0));
        verify(loanProductService).getProductsForFarmer();
    }
}