package com.zhou.seckill.Controller;

import com.zhou.seckill.Domain.SeckillOrder;
import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Redis.AccessKey;
import com.zhou.seckill.Redis.GoodsKey;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.CodeMsg;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.GoodsService;
import com.zhou.seckill.Service.OrderService;
import com.zhou.seckill.Service.SeckillService;
import com.zhou.seckill.Service.SeckillUserService;
import com.zhou.seckill.access.AccessLimit;
import com.zhou.seckill.rabbitmq.MQSender;
import com.zhou.seckill.rabbitmq.SeckillMessage;
import com.zhou.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean  {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender sender;

    private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();
    /*
    * 系统初始化
    * */
    @Override
    public void afterPropertiesSet() throws Exception {
        //将商品数量加载到缓存中
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList==null){
            return;
        }
        for (GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }
    /*
    * GET 幂等的，
    * POST，
    * */
    @RequestMapping(value = "/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(Model model, SeckillUser user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path) {
        model.addAttribute("user",user);
        //看是否登录
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = seckillService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记来减少redis访问，防止执行redisService.decr
        Boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        if (stock<0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);
        return Result.success(0);

        /*
        //判断库存
        GoodsVo goods  = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //库存有，也没秒杀到
        //减库存 下订单 写入秒杀订单
        //原子性
        return seckillService.seckill(user,goods);
        //进入订单详情页
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goods);

         */
    }

    /*
    * orderId:成功
    * -1:秒杀失败
    * 0:排队中
    * */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser user,
                                   @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        //看是否登录
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断用户有没有秒杀到商品，返回订单id
        long result = seckillService.getSeckillResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request,Model model, SeckillUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode) {
        //看是否登录
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查询访问的次数
//        String uri = request.getRequestURI();
//        String key = uri+"_"+user.getId();
//        Integer count = redisService.get(AccessKey.access, key, Integer.class);
//        if (count == null){//第一次访问
//            redisService.set(AccessKey.access, key, 1);
//        }else if(count < 5){
//            redisService.incr(AccessKey.access, key);
//        }else{
//            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//        }
        //验证码验证
        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = seckillService.createSeckillPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response,Model model, SeckillUser user,
                                               @RequestParam("goodsId")long goodsId) {
        //看是否登录
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = seckillService.createVerifyCode(user,goodsId);
        try{
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_OVER);
        }
    }
}
