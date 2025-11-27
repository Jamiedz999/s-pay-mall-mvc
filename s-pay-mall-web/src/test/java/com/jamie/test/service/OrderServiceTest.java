package com.jamie.test.service;


import com.alibaba.fastjson.JSON;
import com.jamie.domain.req.ShopCartReq;
import com.jamie.domain.res.PayOrderRes;
import com.jamie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
    
    @Resource
    private IOrderService orderService;
    @Test
    public void test() throws Exception {
        ShopCartReq shopCartReq = new ShopCartReq();
        shopCartReq.setUserId("Jamie");
        shopCartReq.setProductId("10001");
        PayOrderRes payOrderRes = orderService.createOrder(shopCartReq);
        log.info("request param: {}", JSON.toJSONString(shopCartReq));
        log.info("test result: {}", JSON.toJSONString(payOrderRes));

    }
    
}
