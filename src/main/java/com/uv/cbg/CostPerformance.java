package com.uv.cbg;

import java.math.BigDecimal;

/**
 * @author uvsun 2019-08-02 07:20
 */
public class CostPerformance {
    private BigDecimal score;
    private BigDecimal price;

    public boolean isCost(BigDecimal gamerScore, BigDecimal gamerPrice) {

        if (gamerPrice.compareTo(price) <= 0 && gamerScore.compareTo(score) >= 0) {
            return true;
        }

        return false;
    }

    public CostPerformance() {
    }

    public CostPerformance(BigDecimal score, BigDecimal price) {
        this.score = score;
        this.price = price;
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("3").compareTo(new BigDecimal(4)));
        System.out.println(new BigDecimal("3").compareTo(new BigDecimal("3.5")));
        System.out.println(new BigDecimal("3").compareTo(new BigDecimal("3.0")));
    }
}
