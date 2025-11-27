package com.jamie.service.impl;

import com.google.common.cache.Cache;
import com.jamie.domain.vo.WeixinTemplateMessageVO;
import com.jamie.domain.req.WeixinQrCodeReq;
import com.jamie.domain.res.WeixinQrCodeRes;
import com.jamie.domain.res.WeixinTokenRes;
import com.jamie.service.ILoginService;
import com.jamie.service.weixin.IWeixinApiService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinLoginServiceImpl implements ILoginService {

    @Value("${weixin.config.app-id}")
    private String appid;

    @Value("${weixin.config.app-secret}")
    private String appSecret;

    @Value("${weixin.config.template-id}")
    private String template_id;

    @Resource
    private Cache<String, String> weixinAccessToken;

    @Resource
    private IWeixinApiService weixinApiService;


    @Resource
    private Cache<String, String> openidToken;

    @Override
    public String createQrCodeTicket() throws Exception {

        String accessToken = weixinAccessToken.getIfPresent(appid);

        if(null == accessToken){
            Call<WeixinTokenRes> call = weixinApiService.getToken("client_credential", appid, appSecret);

            WeixinTokenRes weixinTokenRes = call.execute().body();

            assert weixinTokenRes!=null;
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid, accessToken);

        }

        WeixinQrCodeReq weixinQrCodeReq = WeixinQrCodeReq.builder()
                .expire_seconds(2592000)
                .action_name(WeixinQrCodeReq.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeReq.ActionInfo.builder()
                        .scene(WeixinQrCodeReq.ActionInfo.Scene.builder()
                                .scene_id(100601)
                                .build())
                        .build())
                .build();

        Call<WeixinQrCodeRes> call = weixinApiService.createQrCode(accessToken, weixinQrCodeReq);

        WeixinQrCodeRes weixinQrCodeRes = call.execute().body();
        assert null != weixinQrCodeRes;
        return weixinQrCodeRes.getTicket();

    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        openidToken.put(ticket, openid);

        String accessToken = weixinAccessToken.getIfPresent(appid);

        if(null == accessToken){
            Call<WeixinTokenRes> call = weixinApiService.getToken("client_credential", appid, appSecret);

            WeixinTokenRes weixinTokenRes = call.execute().body();

            assert weixinTokenRes!=null;
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid, accessToken);

        }

        // 2. 发送模板消息
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageVO.put(data, WeixinTemplateMessageVO.TemplateKey.USER, openid);

        WeixinTemplateMessageVO templateMessageDTO = new WeixinTemplateMessageVO(openid, template_id);
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();

    }
}
