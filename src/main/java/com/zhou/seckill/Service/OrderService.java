package com.zhou.seckill.Service;

import com.zhou.seckill.DAO.GoodsDao;
import com.zhou.seckill.DAO.OrderDao;
import com.zhou.seckill.Domain.Goods;
import com.zhou.seckill.Domain.OrderInfo;
import com.zhou.seckill.Domain.SeckillOrder;
import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Redis.OrderKey;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        //return  orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        //做优化，查缓存
        return redisService.get(OrderKey.getSeckillOrderByUidGid,""+userId+"_"+goodsId,SeckillOrder.class);

    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public Result<OrderInfo> createOrder(SeckillUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());

        orderDao.insertSeckillOrder(seckillOrder);

        //生成订单后写入缓存
        redisService.set(OrderKey.getSeckillOrderByUidGid,""+user.getId()+"_"+goods.getId(),seckillOrder);


        return Result.success(orderInfo);
    }


}
