package com.zhou.seckill.Controller;

import com.zhou.seckill.Domain.User;
import com.zhou.seckill.Redis.KeyPrefix;
import com.zhou.seckill.Redis.RedisService;
import com.zhou.seckill.Redis.UserKey;
import com.zhou.seckill.Result.Result;
import com.zhou.seckill.Service.UserService;
import com.zhou.seckill.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

//    @RequestMapping("/mq/header")
//    @ResponseBody
//    public Result<String> header(){
//        sender.sendHeader("hello,imooc");
//        return Result.success("hello");
//    }
//
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout(){
//        sender.sendFanout("hello,imooc");
//        return Result.success("hello");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic(){
//        sender.sendTopic("hello,imooc");
//        return Result.success("hello");
//    }
//
//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(){
//        sender.send("hello,imooc");
//        return Result.success("hello");
//    }
//
//    @RequestMapping("/redis/get")
//    @ResponseBody
//    public Result<User> redisGet(){
//        User user = redisService.get(UserKey.getById,""+1,User.class);
//        return Result.success(user);
//    }
//
//    @RequestMapping("/redis/set")
//    @ResponseBody
//    public Result<Boolean> redisSet(){
//        User user = new User();
//        user.setName("alsadko");
//        user.setId(1);
//        redisService.set(UserKey.getById,""+1,user);
//        return Result.success(true);
//    }

}
