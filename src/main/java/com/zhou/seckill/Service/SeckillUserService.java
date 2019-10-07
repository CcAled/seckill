package com.zhou.seckill.Service;

import com.zhou.seckill.DAO.SeckillUserDao;
import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Domain.User;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Redis.SeckillUserKey;
import com.zhou.seckill.Result.CodeMsg;
import com.zhou.seckill.exception.GlobalException;
import com.zhou.seckill.util.MD5Util;
import com.zhou.seckill.util.UUIDUtil;
import com.zhou.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    public SeckillUser getById(long id){
        //取缓存
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
        if (user!=null){
            return user;
        }
        //取数据库
        user = seckillUserDao.getById(id);
        if (user != null){
            redisService.set(SeckillUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String formPass){
        //取user
        SeckillUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //创建新对象更新，防止更新过多语句
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass,user.getSalt()));
        seckillUserDao.update(toBeUpdate);
        //处理缓存,token和id的缓存
        redisService.delete(SeckillUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(SeckillUserKey.token,token,user);
        return true;
    }

    public SeckillUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser user =  redisService.get(SeckillUserKey.token,token,SeckillUser.class);
        //延长有效期
        //判断用户是否为空
        if(user != null){
            addCookie(response,token,user);
        }
        return user;
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
//        String token = UUIDUtil.uuid();
//        redisService.set(SeckillUserKey.token,token,user);
//        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
//        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
//        cookie.setPath("/");
//        response.addCookie(cookie);
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    private void addCookie(HttpServletResponse response,String token,SeckillUser user){

        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
