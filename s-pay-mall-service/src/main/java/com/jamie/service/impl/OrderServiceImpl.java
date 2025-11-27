package com.jamie.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private IOrderDao orderDao;

    @Resource
    private ProductRPC productRPC;

    @Resource
    private AlipayClient alipayClient;


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
            log.info("create order, create payorder. userId:{} productId: {} orderId:{}", shopCartReq.getUserId(), shopCartReq.getProductId(), unpaidOrder.getOrderId());

            PayOrder payOrder =  doPrepayOrder(unpaidOrder.getProductId(), unpaidOrder.getProductName(), unpaidOrder.getOrderId(), unpaidOrder.getTotalAmount());
            return PayOrderRes.builder()
                    .orderId(payOrder.getOrderId())
                    .payUrl(payOrder.getPayUrl())
                    .build();
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
        PayOrder payOrder =  doPrepayOrder(productVO.getProductId(), productVO.getProductName(), orderId, productVO.getPrice());



        return PayOrderRes.builder()
                .orderId(orderId)
                .payUrl(payOrder.getPayUrl())
                .build();

    }

    private PayOrder doPrepayOrder(String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrder payOrder = new PayOrder();
        payOrder.setPayUrl(form);
        payOrder.setOrderId(orderId);
        payOrder.setStatus(Constants.OrderStatusEnum.PAY_WAIT.getCode());

        orderDao.updateOrderPayInfo(payOrder);

        return payOrder;

    }
}
