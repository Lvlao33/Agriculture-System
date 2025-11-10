package com.farmporject.backend.finance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.farmporject.backend.finance.service.LoanProductService;
import com.farmporject.backend.finance.model.LoanProduct;
import java.util.List;

/** 融资-产品列表 */
@RestController
@RequestMapping("/api/finance/loan-products")
public class LoanProductController {

    private final LoanProductService loanProductService;

    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<LoanProduct>> list() {
        List<LoanProduct> products = loanProductService.getProductsForFarmer();
        return ResponseEntity.ok(products);
    }
}
