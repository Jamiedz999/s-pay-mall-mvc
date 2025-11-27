package com.jamie.service.impl;

import com.jamie.common.constants.Constants;
import com.jamie.dao.IOrderDao;
import com.jamie.domain.po.PayOrder;
import com.jamie.domain.req.ShopCartReq;
import com.jamie.domain.res.PayOrderRes;
import com.jamie.domain.vo.ProductVO;
import com.jamie.service.IOrderService;
import com.jamie.service.rpc.ProductRPC;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {

    @Resource
    private IOrderDao orderDao;

    @Resource
    private ProductRPC productRPC;
    @Override
    public PayOrderRes createOrder(ShopCartReq shopCartReq) throws Exception {
        // 1, check if user has unpaid order
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setUserId(shopCartReq.getUserId());
        payOrderReq.setProductId(shopCartReq.getProductId());

        PayOrder unpaidOrder = orderDao.queryUnPayOrder(payOrderReq);

        if(null != unpaidOrder && Constants.OrderStatusEnum.PAY_WAIT.getCode().equals(unpaidOrder.getStatus())){
            log.info("create order, unpaid order exists. userId:{} productId: {} orderId:{}", shopCartReq.getUserId(), shopCartReq.getProductId(), unpaidOrder.getOrderId());
            return PayOrderRes.builder()
                    .orderId(unpaidOrder.getOrderId())
                    .payUrl(unpaidOrder.getPayUrl())
                    .build();
        } else if (null != unpaidOrder && Constants.OrderStatusEnum.CREATE.getCode().equals(unpaidOrder.getStatus())){
            //to do
        }

        //2, query product info and create order
        ProductVO productVO = productRPC.queryProductByProductId(shopCartReq.getProductId());
        String orderId = RandomStringUtils.randomNumeric(16);
        orderDao.insert(PayOrder.builder()
                        .userId(shopCartReq.getUserId())
                        .productId(shopCartReq.getProductId())
                        .productName(productVO.getProductName())
                        .orderId(orderId)
                        .totalAmount(productVO.getPrice())
                        .orderTime(new Date())
                        .status(Constants.OrderStatusEnum.CREATE.getCode())

                .build());
        //3.create pay order

        return PayOrderRes.builder()
                .orderId(orderId)
                .payUrl("no url temporally")
                .build();

    }
}
