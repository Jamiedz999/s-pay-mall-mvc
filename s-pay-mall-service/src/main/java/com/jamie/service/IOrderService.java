package com.jamie.service;


import com.jamie.domain.req.ShopCartReq;
import com.jamie.domain.res.PayOrderRes;

import java.util.List;

public interface IOrderService {


    PayOrderRes createOrder(ShopCartReq shopCartReq) throws Exception;

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);



}
