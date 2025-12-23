package com.farmporject.backend.finance.service;

import org.springframework.stereotype.Service;
import com.farmporject.backend.finance.repository.LoanProductRepository;
import com.farmporject.backend.finance.repository.LoanIntentionRepository;
import com.farmporject.backend.finance.model.LoanProduct;
import com.farmporject.backend.finance.model.LoanIntention;
import com.farmporject.backend.finance.dto.ComboRecommendationDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class LoanProductService {
    private final LoanProductRepository loanProductRepository;

    public LoanProductService(LoanProductRepository loanProductRepository) {
        this.loanProductRepository = loanProductRepository;
    }

    /* 产品信息展示 */
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

    /* 联合贷款相关 */
    // 根据两位用户意向推荐产品
    @Autowired
    private LoanIntentionRepository loanIntentionRepository;

    public ComboRecommendationDTO recommendCombo(Long userId, Long partnerId) {
        // 1. 获取两个用户的意向
        Optional<LoanIntention> userIntentionOpt = loanIntentionRepository.findByUserId(userId);
        Optional<LoanIntention> partnerIntentionOpt = loanIntentionRepository.findByUserId(partnerId);

        if (userIntentionOpt.isEmpty() || partnerIntentionOpt.isEmpty()) {
            throw new RuntimeException("无法找到用户或联合贷款人的融资意向信息");
        }

        LoanIntention userIntention = userIntentionOpt.get();
        LoanIntention partnerIntention = partnerIntentionOpt.get();

        // 2. 计算合并年收入
        BigDecimal income1 = userIntention.getAnnualIncome();
        BigDecimal income2 = partnerIntention.getAnnualIncome();
        if (income1 == null)
            income1 = BigDecimal.ZERO;
        if (income2 == null)
            income2 = BigDecimal.ZERO;

        BigDecimal combinedIncome = income1.add(income2);

        // 3. 筛选产品
        // 这里简单获取所有产品并在内存中筛选，实际应用中应该使用自定义查询
        List<LoanProduct> allProducts = loanProductRepository.findAll();

        List<LoanProduct> recommended = allProducts.stream()
                .filter(p -> p
                        .getProductStatus() == com.farmporject.backend.finance.model.LoanProduct.ProductStatus.SALE)
                .filter(p -> {
                    // 如果产品设置了最低准入收入，则进行判断
                    if (p.getMinAnnualIncome() != null) {
                        return combinedIncome.compareTo(p.getMinAnnualIncome()) >= 0;
                    }
                    return true; // 没有设置门槛则默认通过
                })
                .sorted((p1, p2) -> {
                    // 优先推荐额度高的
                    return p2.getAmount().compareTo(p1.getAmount());
                })
                .limit(3)
                .collect(Collectors.toList());

        String reason = String.format("您的合并年收入达到 %s 元，为您精选了 %d 款高额度产品。",
                combinedIncome.toString(), recommended.size());

        return new ComboRecommendationDTO(recommended, combinedIncome, reason);
    }
}
