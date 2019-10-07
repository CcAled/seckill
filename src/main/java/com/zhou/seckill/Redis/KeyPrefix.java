package com.zhou.seckill.Redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
