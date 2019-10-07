package com.zhou.seckill.DAO;

import com.zhou.seckill.Domain.OrderInfo;
import com.zhou.seckill.Domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from seckill_order where user_id=#{userId} and goods_id=#{goodsId}")
    public SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId") long userId,@Param("goodsId") long goodsId);


    /*
     * statement="select last_insert-id()":表示定义的子查询语句
     * before=false：表示在之后执行
     * keyColumn="id":表示查询所返回的类名
     * resultType=long.class：表示返回值得类型
     * keyProperty="id" ：表示将该查询的属性设置到某个列中，此处设置到empNo中
     */
    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date)values("
                                + "#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    //找出上次插入的id
    public long insert(OrderInfo orderInfo);

    @Insert("insert into seckill_order (user_id,goods_id,order_id)values(#{userId},#{goodsId},#{orderId})")
    public int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(@Param("orderId") long orderId);
}
