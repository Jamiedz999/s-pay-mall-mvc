package com.jamie.dao;

import com.jamie.domain.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IOrderDao {

    void insert(PayOrder payOrder);

    PayOrder queryUnPayOrder(PayOrder payOrderReq);

    void updateOrderPayInfo(PayOrder payOrder);
}
