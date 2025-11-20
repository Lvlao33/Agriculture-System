package com.farmporject.backend.finance.service;

import com.farmporject.backend.finance.model.LoanProduct;
import com.farmporject.backend.finance.repository.LoanProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanProductServiceTest {

    @Mock
    private LoanProductRepository loanProductRepository;

    @InjectMocks
    private LoanProductService loanProductService;

    @Test
    void getProductsForFarmer() {
        LoanProduct product = new LoanProduct();
        product.setId(1L);
        product.setName("农资贷");
        product.setBank("ABC");
        product.setAmount(BigDecimal.valueOf(100000));
        product.setTerm(12);
        product.setRate(BigDecimal.valueOf(3.5));
        product.setFastestDisbursement("3 days");
        product.setDescription("full");

        when(loanProductRepository.findAll()).thenReturn(List.of(product));

        List<LoanProduct> result = loanProductService.getProductsForFarmer();

        assertEquals(1, result.size());
        LoanProduct projected = result.get(0);
        assertEquals(product.getName(), projected.getName());
        assertEquals(product.getBank(), projected.getBank());
        assertNull(projected.getDescription());
        assertNotSame(product, projected);
        verify(loanProductRepository).findAll();
    }

    @Test
    void getProductsForAdmin() {
        when(loanProductRepository.findAll()).thenReturn(List.of(new LoanProduct()));

        List<LoanProduct> result = loanProductService.getProductsForAdmin();

        assertEquals(1, result.size());
        verify(loanProductRepository).findAll();
    }
}
