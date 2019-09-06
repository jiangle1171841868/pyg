package com.itheima.reduce

import com.itheima.bean.ChannelPvuv
import org.apache.flink.api.common.functions.{ReduceFunction}

//reduce 聚合 输入和输出类型一样
class ChannelPvuvReduce extends ReduceFunction[ChannelPvuv] {

  override def reduce(value1: ChannelPvuv, value2: ChannelPvuv): ChannelPvuv = {

    val pvuv = new ChannelPvuv
    pvuv.setChannelId(value1.channelId)
    pvuv.setTimeFormat(value1.timeFormat)
    pvuv.setPv(value1.pv + value2.pv)
    pvuv.setUv(value1.uv + value2.uv)

    pvuv
  }
}
