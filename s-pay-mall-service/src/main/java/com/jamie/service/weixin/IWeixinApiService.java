package com.jamie.service.weixin;

import com.jamie.domain.vo.WeixinTemplateMessageVO;
import com.jamie.domain.req.WeixinQrCodeReq;
import com.jamie.domain.res.WeixinQrCodeRes;
import com.jamie.domain.res.WeixinTokenRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 微信接口服务
 */

public interface IWeixinApiService {

    @GET("cgi-bin/token")
    Call<WeixinTokenRes> getToken( @Query("grant_type") String grantType,
                                  @Query("appid") String appid,
                                  @Query("secret") String appSecret);

    @POST("cgi-bin/qrcode/create")
    Call<WeixinQrCodeRes> createQrCode(@Query("access_token") String accessToken, @Body WeixinQrCodeReq weixinQrCodeReq);

    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessageVO weixinTemplateMessageVO);

}

