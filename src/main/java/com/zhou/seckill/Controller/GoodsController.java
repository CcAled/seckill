package com.zhou.seckill.Controller;


import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Domain.User;
import com.zhou.seckill.Redis.GoodsKey;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.GoodsService;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.vo.GoodsDetailVo;
import com.zhou.seckill.vo.GoodsVo;
import com.zhou.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;


    @RequestMapping(value = "/to_list",produces="text/html")
    @ResponseBody
    public String list(HttpServletRequest request,HttpServletResponse response,Model model, SeckillUser user) {
        // model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);

        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        //不为空就存到redis中去
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        //返回页面
        return html;
        //return "goods_list";
        //        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
        ////            return "login";
        ////        }
        ////        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        ////        SeckillUser user = userService.getByToken(response,token);
        ////        model.addAttribute("user",user);
    }


    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, SeckillUser user,
                                        @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus = 0;//秒杀状态
        int remainSeconds = 0;//剩余多少秒开始秒杀

        if(now < startAt){//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt-now)/1000);
        }else if (now>endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);
        return Result.success(vo);
        //return "goods_detail";


        //        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
        //            return "login";
        //        }
        //        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //        SeckillUser user = userService.getByToken(response,token);
        //        model.addAttribute("user",user);
    }

    @RequestMapping(value = "/to_detail2/{goodsId}",produces="text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request,HttpServletResponse response,
                         Model model, SeckillUser user,
                         @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user",user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;//秒杀状态
        int remainSeconds = 0;//剩余多少秒开始秒杀

        if(now < startAt){//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt-now)/1000);
        }else if (now>endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        //不为空就存到redis中去
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
        //return "goods_detail";


        //        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
        //            return "login";
        //        }
        //        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //        SeckillUser user = userService.getByToken(response,token);
        //        model.addAttribute("user",user);
    }


}
