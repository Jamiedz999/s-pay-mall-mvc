package com.jamie.job;


import com.jamie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec(){
        try{
            log.info("job timout 30 min close order");
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if(null == orderIds || orderIds.isEmpty()) {
                log.info("no timout order for the moment");
                return;
            }

            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);
                log.info("close order: orderId:{}, status:{}", orderId,status);
            }
        }catch (Exception e) {
            log.error("failed", e);
        }
    }

}
