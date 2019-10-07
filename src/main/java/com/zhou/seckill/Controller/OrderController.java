package com.zhou.seckill.Controller;


import com.zhou.seckill.Domain.OrderInfo;
import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.CodeMsg;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.GoodsService;
import com.zhou.seckill.Service.OrderService;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.vo.GoodsVo;
import com.zhou.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    //@NeedLogin
    //TODO 拦截器
    public Result<OrderDetailVo> info(Model model, SeckillUser user,
                                      @RequestParam("orderId")long orderId) {
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
