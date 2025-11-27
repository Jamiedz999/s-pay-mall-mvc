package com.jamie.service;


import com.jamie.domain.req.ShopCartReq;
import com.jamie.domain.res.PayOrderRes;

public interface IOrderService {


    PayOrderRes createOrder(ShopCartReq shopCartReq) throws Exception;
}
