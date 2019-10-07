package com.zhou.seckill.config;

import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.access.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//1.SpringMVC解析器用于解析request请求参数并绑定数据到Controller的入参上。
//2.自定义一个参数解析器需要实现HandlerMethodArgumentResolver接口，重写supportsParameter和resolveArgument方法，配置文件中加入resolver配置。(WebConfig实现)
//3.如果需要多个解析器同时生效需要在一个解析器中对其他解析器做兼容。

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    SeckillUserService userService;

    @Override
    //判断是否支持要转换的参数类型
    public boolean supportsParameter(MethodParameter methodParameter) {
        //如果函数包含我们的自定义注解，那就走resolveArgument()函数
        return methodParameter.getParameterType() == SeckillUser.class;
    }

    @Override
    //当支持后进行相应的转换
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//
//        String paramToken = request.getParameter(SeckillUserService.COOKI_NAME_TOKEN);
//        String cookieToken = getCookieValue(request,SeckillUserService.COOKI_NAME_TOKEN);
//        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
//            return null;
//        }
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        return userService.getByToken(response,token);

//        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
//            return "login";
//        }
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        SeckillUser user = userService.getByToken(response,token);

    }

//    private String getCookieValue(HttpServletRequest request, String cookiName) {
//        Cookie[] cookies = request.getCookies();
//        if(cookies == null || cookies.length <= 0){
//            return null;
//        }
//        for (Cookie cookie:cookies){
//            if(cookie.getName().equals(cookiName)){
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
}
