package com.jamie.service.weixin;

import java.io.IOException;

public interface ILoginService {

    String createQrCodeTicket() throws Exception;

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openId) throws IOException;
}
