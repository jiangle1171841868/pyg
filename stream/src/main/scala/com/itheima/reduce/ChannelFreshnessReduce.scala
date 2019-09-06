package com.itheima.reduce

import com.itheima.bean.ChannelFreshness
import org.apache.flink.api.common.functions.ReduceFunction

class ChannelFreshnessReduce extends ReduceFunction[ChannelFreshness] {


  override def reduce(value1: ChannelFreshness, value2: ChannelFreshness): ChannelFreshness = {

    //聚合新老用户
    val freshness = new ChannelFreshness
    freshness.setChannelId(value1.channelId)
    freshness.setTimeFormat(value1.timeFormat)
    freshness.setNewCount(value1.newCount + value2.newCount)
    freshness.setOldCount(value1.oldCount + value2.oldCount)
    freshness

  }
}
