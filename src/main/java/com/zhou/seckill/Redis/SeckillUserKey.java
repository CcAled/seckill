package com.zhou.seckill.Redis;

public class SeckillUserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600*24*2;//两天
    public SeckillUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE,"tk");
    public static SeckillUserKey getById = new SeckillUserKey(0,"id");

}
