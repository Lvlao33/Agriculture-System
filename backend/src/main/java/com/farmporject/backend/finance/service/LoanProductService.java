package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import com.farmporject.backend.finance.repository.LoanProductRepository;
import com.farmporject.backend.finance.model.LoanProduct;
import java.util.List;
import java.util.ArrayList;

@Service
public class LoanProductService {
    private final LoanProductRepository loanProductRepository;

    public LoanProductService(LoanProductRepository loanProductRepository) {
        this.loanProductRepository = loanProductRepository;
    }

    // 农户端：返回前端所需产品信息
    // 农户端只需：id、name、bank、amount、term、rate、fastestDisbursement
    public List<LoanProduct> getProductsForFarmer() {
        List<LoanProduct> products = loanProductRepository.findAll();
        List<LoanProduct> farmerProducts = new ArrayList<>();
        for (LoanProduct product : products) {
            farmerProducts.add(new LoanProduct(product.getId(), product.getName(), product.getBank(),
                    product.getAmount(), product.getTerm(), product.getRate(), product.getFastestDisbursement()));
        }
        return farmerProducts;
    }

    // 管理端：返回前端所需产品信息
    // 全部信息
    public List<LoanProduct> getProductsForAdmin() {
        return loanProductRepository.findAll();
    }

}
