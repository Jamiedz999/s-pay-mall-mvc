package com.jamie.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private String productId;
    private String productName;
    private String productDesc;
    private BigDecimal price;
}
