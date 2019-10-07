package com.zhou.seckill.Controller;


import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.GoodsService;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;


    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser user) {
        return Result.success(user);
    }


//    @RequestMapping("/to_detail/{goodsId}")
//    public String detail(Model model, SeckillUser user,
//                         @PathVariable("goodsId")long goodsId) {
//        model.addAttribute("user",user);
//
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods",goods);
//
//        //
//        long startAt = goods.getStartDate().getTime();
//        long endAt = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//
//        int seckillStatus = 0;//秒杀状态
//        int remainSeconds = 0;//剩余多少秒开始秒杀
//
//        if(now < startAt){//秒杀还没开始，倒计时
//            seckillStatus = 0;
//            remainSeconds = (int)((startAt-now)/1000);
//        }else if (now>endAt){//秒杀已经结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        }else{//秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//
//        model.addAttribute("seckillStatus",seckillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//        return "goods_detail";
        //        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
        //            return "login";
        //        }
        //        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //        SeckillUser user = userService.getByToken(response,token);
        //        model.addAttribute("user",user);
//    }


}
