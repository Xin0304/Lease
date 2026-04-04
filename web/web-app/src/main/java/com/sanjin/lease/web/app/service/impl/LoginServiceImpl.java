package com.sanjin.lease.web.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.redisconstant.RedisConstant;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.common.utils.StpAppUtil;
import com.sanjin.lease.common.utils.VerifyCodeUtil;
import com.sanjin.lease.model.entity.UserInfo;
import com.sanjin.lease.web.app.mapper.UserInfoMapper;
import com.sanjin.lease.web.app.service.LoginService;
import com.sanjin.lease.web.app.vo.user.LoginVo;
import com.sanjin.lease.web.app.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private UserInfoMapper userInfoMapper;

    //获取验证码
    @Override
    public void getCode(String phone) throws IOException {

//        //验证手机号是否为空
//        if (!StringUtils.hasText(phone)){
//            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
//        }
//
//
//        //判断验证码是否存在
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
//        Boolean hasKey = redisTemplate.hasKey(key);
//        if (hasKey){
//            //若存在，则检查其存在的时间
//            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
//            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - expire <
//                    RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC){
//                //若存在时间不足一分钟，响应发送过于频繁
//                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
//            }
//        }

        String verifyCode = VerifyCodeUtil.getVerifyCode(4);
//        this.sendMessage(phone,verifyCode);
        redisTemplate.opsForValue().set(key,"1234",RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);
    }

    //用户登录
    @Override
    public String userLogin(LoginVo loginVo) {

//        //验证手机号
//        if (!StringUtils.hasText(loginVo.getPhone())) {
//            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
//        }
//        if (!StringUtils.hasText(loginVo.getCode())) {
//            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
//        }
//
//        //获取redis验证码
//        String key = RedisConstant.APP_LOGIN_PREFIX + loginVo.getPhone();
//        String redis_code = redisTemplate.opsForValue().get(key);
//
//        //验证码不存在
//        if (redis_code == null){
//            throw new LeaseException(ResultCodeEnum.CAPTCHA_NOT_FOUND);
//        }
//        //验证码验证
//        if (!redis_code.equals(loginVo.getCode())){
//            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
//        }

        //验证用户
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone,loginVo.getPhone());
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);
//
//        //创建用户 判断是否是第一次登录
//        if (userInfo == null){
//            userInfo = new UserInfo();
//            userInfo.setPhone(loginVo.getPhone());
//            userInfo.setStatus(BaseStatus.ENABLE);
//            userInfoMapper.insert(userInfo);
//        }
//
//        //验证用户状态
//        if (userInfo.getStatus().equals(BaseStatus.DISABLE)){
//            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
//        }

        StpAppUtil.login(userInfo.getId());

        return StpAppUtil.getTokenValue();
    }


    @Override
    public UserInfoVo getUserInfoId(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }


        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
    }

    @Override
    public String getUserPhone(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        return userInfo.getPhone();
    }

    private void sendMessage(String phone, String verifyCode) throws IOException {
        String url = "https://smssend.shumaidata.com/sms/send";
        String appCode = "a18affdef4d34e2485acd6db37fa0bc8";

        Map<String, String> params = new HashMap<>();
        params.put("receive", phone);
        params.put("tag", verifyCode);
        params.put("templateId", "ada3680fce94126321470cf55744222a");

        String result = postForm(appCode, url, params);
        System.out.println(result);
    }
    public static String postForm(String appCode, String url, Map<String, String> params) throws IOException {
        url = url + buildRequestUrl(params);
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            formbuilder.add(key, params.get(key));
        }
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE " + appCode).post(body).build();
        Response response = client.newCall(request).execute();
        System.out.println("返回状态码" + response.code() + ",message:" + response.message());
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IOException("短信服务响应 body 为空");
        }
        String result = responseBody.string();
        return result;
    }
    public static String buildRequestUrl(Map<String, String> params) {
        StringBuilder url = new StringBuilder("?");
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            url.append(key).append("=").append(params.get(key)).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
    }
}
