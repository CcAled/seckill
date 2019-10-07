package com.zhou.seckill.DAO;

import com.zhou.seckill.Domain.Goods;
import com.zhou.seckill.Domain.SeckillGoods;
import com.zhou.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.seckill_price from seckill_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.seckill_price from seckill_goods mg left join goods g on mg.goods_id = g.id where g.id=#{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    //减库存时stock_count > 0防止库存变为负数，并且在seckill_order上建立索引user_id和goods_id的唯一索引，防止同一个用户下两次单
    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(SeckillGoods g);
}
