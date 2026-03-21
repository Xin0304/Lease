package com.sanjin.lease.web.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.common.utils.JwtUtil;
import com.sanjin.lease.model.entity.UserInfo;
import com.sanjin.lease.model.enums.BaseStatus;
import com.sanjin.lease.web.app.mapper.UserInfoMapper;
import com.sanjin.lease.web.app.service.LoginService;
import com.sanjin.lease.web.app.service.UserInfoService;
import com.sanjin.lease.web.app.utils.VerifyCodeUtil;
import com.sanjin.lease.web.app.vo.user.LoginVo;
import com.sanjin.lease.web.app.vo.user.UserInfoVo;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;



    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserInfoService userInfoService;

    //获取验证码
    @Override
    public void getCode(String phone) throws IOException {
        String verifyCode = VerifyCodeUtil.getVerifyCode(4);
        this.sendMessage(phone,verifyCode);
        redisTemplate.opsForValue().set(phone,verifyCode,5, TimeUnit.HOURS);
    }

    //获取验证码登录
    @Override
    public String login(LoginVo loginVo) {

        //1 从loginVo获取验证码和手机号，非空判断
        if (StringUtils.hasText( loginVo.getPhone())){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }
        if (StringUtils.hasText(loginVo.getCode())){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }
        //2 判断验证码是否正确
        // 把redis里面验证码 和 输入比对，redis的key是手机号
        String redis_code = redisTemplate.opsForValue().get(loginVo.getPhone());
        if (redis_code == null){
            throw new LeaseException("验证码失败",222);
        }
        //3 验证码不相同，提示用户
        if (!redis_code.equals(loginVo.getCode())){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }
        //4 验证码相同，判断当前手机号是否第一次登录
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone,loginVo.getPhone());
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);



        //5 如果当前手机号是第一次登录，注册，把手机号信息添加用户表
                //把手机号信息添加用户表
        if (userInfo == null){
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户-"+loginVo.getPhone().substring(5));
            userInfoMapper.insert(userInfo);
        }
        //6 判断用户是否被禁用
        if (userInfo.getStatus().equals(BaseStatus.ENABLE)){
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        //7 jwt生成token，返回
        String token = JwtUtil.createToken(userInfo.getId(), userInfo.getPhone());
        return token;
    }

    //获取登录用户信息
    @Override
    public UserInfoVo findLoginUserInfo(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
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
        String result = response.body().string();
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
