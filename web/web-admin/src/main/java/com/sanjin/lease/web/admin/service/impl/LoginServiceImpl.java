package com.sanjin.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.redisconstant.RedisConstant;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.common.utils.StpAdminUtil;
import com.sanjin.lease.model.entity.SystemUser;
import com.sanjin.lease.model.enums.BaseStatus;
import com.sanjin.lease.web.admin.mapper.SystemUserMapper;
import com.sanjin.lease.web.admin.service.LoginService;
import com.sanjin.lease.web.admin.service.SystemUserService;
import com.sanjin.lease.web.admin.vo.login.CaptchaVo;
import com.sanjin.lease.web.admin.vo.login.LoginVo;
import com.sanjin.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SystemUserMapper systemUserMapper;
    @Autowired
    private SystemUserService systemUserService;

    //获取图形验证码
    @Override
    public CaptchaVo getCaptcha() {
        //创建验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(138, 48,4);
        //设置验证码的类型
        specCaptcha.setCharType(SpecCaptcha.TYPE_DEFAULT);
        //获取验证码并转为小写
        String code = specCaptcha.text().toLowerCase();

        //把生成的验证码图片转换成 Base64 字符串
        String image = specCaptcha.toBase64();
        //生成一个唯一的 Redis key
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();

        //把验证码保存到 Redis 中
        redisTemplate.opsForValue().set(key, code, 60, TimeUnit.HOURS);

        //返回验证码图片和 key
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setKey(key);
        captchaVo.setImage(image);
        return captchaVo;
    }

    @Override
    public String login(LoginVo loginVo) {
        //获取验证码
        String captcha = loginVo.getCaptchaCode();
        //2 判断验证码是否为空，如果为空，提示用户
        if (!StringUtils.hasText(captcha)){
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }

        //3 如果验证码不为空，从redis根据loginVo里面key获取redis存储验证码
        String captchaKey = loginVo.getCaptchaKey();
        String redis_Captcha = redisTemplate.opsForValue().get(captchaKey);

        //4 如果根据key获取redis验证码为空，提示用户
        if (!StringUtils.hasText(redis_Captcha)){
            throw new LeaseException(ResultCodeEnum.CAPTCHA_NOT_FOUND);
        }

        //5 如果根据key获取redis验证码不为空，校验验证码
        // 把redis的验证码 和 输入的验证码比对，如果不同，提示用户
        if (!redis_Captcha.equals(captcha)){
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }

        //6 根据loginVo里面用户名查询数据库，如果查询结果为空，提示用户
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUsername,username);
        wrapper.eq(SystemUser::getIsDeleted, 0);
        SystemUser systemUser = systemUserMapper.selectOne(wrapper);
        if (systemUser == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        //7 如果查询结果不为空，判断用户是否被禁用，如果被禁用，提示用户
        if (systemUser.getStatus() == BaseStatus.DISABLE){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }

        //8如果用户没有禁用，比较密码
        // 把数据库存储密码 和输入的密码比对，输入密码进行加密之后再比对
        String database_Password = systemUser.getPassword();
        String loginVoPassword = loginVo.getPassword();

        //输入密码加密
        String passWord_md5Hex = DigestUtils.md5Hex(loginVoPassword);

        ////9 如果密码不相同，提示用户
        if (!database_Password.equals(passWord_md5Hex)){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        //10 使用sa-token登录
        StpAdminUtil.login(systemUser.getId());
        return StpAdminUtil.getTokenValue();

    }

    @Override
    public SystemUserInfoVo findSystemUserInfo(Long id) {

        SystemUser user = systemUserService.getById(id);
        return new SystemUserInfoVo(user.getName(),user.getAvatarUrl());
    }


}
