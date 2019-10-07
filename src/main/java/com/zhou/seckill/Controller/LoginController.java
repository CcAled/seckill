package com.zhou.seckill.Controller;


import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.CodeMsg;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.Service.UserService;
import com.zhou.seckill.util.ValidatorUtil;
import com.zhou.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        //参数校验
//        String passInput = loginVo.getPassword();
//        String mobile = loginVo.getMobile();
//        if(StringUtils.isEmpty(passInput)){//为空，返回错误页面
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile)){//为空，返回错误页面
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){//手机号格式错误
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }
        //使用validator进行参数校验
        //登录
        userService.login(response,loginVo);
        return Result.success(true);
    }

}
