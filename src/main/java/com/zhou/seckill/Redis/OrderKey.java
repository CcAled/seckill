package com.zhou.seckill.Redis;

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super( prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey("moug");
}
