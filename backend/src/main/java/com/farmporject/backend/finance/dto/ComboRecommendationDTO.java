package com.farmporject.backend.finance.dto;

import com.farmporject.backend.finance.model.LoanProduct;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ComboRecommendationDTO implements Serializable {
    private List<LoanProduct> recommendedProducts;
    private BigDecimal combinedIncome;
    private String recommendationReason;

    public ComboRecommendationDTO() {
    }

    public ComboRecommendationDTO(List<LoanProduct> recommendedProducts, BigDecimal combinedIncome,
            String recommendationReason) {
        this.recommendedProducts = recommendedProducts;
        this.combinedIncome = combinedIncome;
        this.recommendationReason = recommendationReason;
    }

    public List<LoanProduct> getRecommendedProducts() {
        return recommendedProducts;
    }

    public void setRecommendedProducts(List<LoanProduct> recommendedProducts) {
        this.recommendedProducts = recommendedProducts;
    }

    public BigDecimal getCombinedIncome() {
        return combinedIncome;
    }

    public void setCombinedIncome(BigDecimal combinedIncome) {
        this.combinedIncome = combinedIncome;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
}
