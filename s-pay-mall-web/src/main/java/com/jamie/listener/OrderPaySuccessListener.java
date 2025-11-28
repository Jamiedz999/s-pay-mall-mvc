package com.jamie.listener;


import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPaySuccessListener {
    @Subscribe
    public void handleEvent(String paySuccessMessage) {
        log.info("got a pay success msg, you can do the folllowing: ");
    }
}
