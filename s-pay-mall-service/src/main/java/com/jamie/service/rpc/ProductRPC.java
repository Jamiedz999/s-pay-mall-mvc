package com.jamie.service.rpc;

import com.jamie.domain.vo.ProductVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service

public class ProductRPC {
    public ProductVO queryProductByProductId(String product) {

        ProductVO productVO = new ProductVO();
        productVO.setProductId(product);
        productVO.setProductName("TestProduct");
        productVO.setProductDesc("This is a test product");
        productVO.setPrice(new BigDecimal("1.68"));
        return productVO;
    }
}
