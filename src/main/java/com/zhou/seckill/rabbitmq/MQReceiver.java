package com.zhou.seckill.rabbitmq;

import com.zhou.seckill.Domain.SeckillOrder;
import com.zhou.seckill.Domain.SeckillUser;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.GoodsService;
import com.zhou.seckill.Service.OrderService;
import com.zhou.seckill.Service.SeckillService;
import com.zhou.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        //从队列中拉取
        SeckillMessage mm = RedisService.stringtoBean(message, SeckillMessage.class);
        SeckillUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        //减库存 封装订单
        GoodsVo goods  = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if(stock <= 0){
            return;
        }
        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return;
        }
        //库存有，也没秒杀到
        //减库存 下订单 写入秒杀订单
        //原子性
        seckillService.seckill(user,goods);
    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        log.info("receive message:"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        log.info("topic queue1 message:"+message);
//    }
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        log.info("topic queue2 message:"+message);
//    }
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeader(byte[] message){
//        log.info("header queue message:"+new String(message));
//    }

}
