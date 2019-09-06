package com.itheima.task

import com.itheima.`trait`.ProcessData
import com.itheima.bean.Message
import com.itheima.map.ChannelFreshnessFlatMap
import com.itheima.reduce.ChannelFreshnessReduce
import com.itheima.sink.ChannelFreshnessSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object ChannelFreshnessTask extends ProcessData {


  override def process(waterData: DataStream[Message]): Unit = {
    waterData
      /// TODO: 1.数据转化  输入Message 输出ChannelFreshness(多个 基于三个维度的数据)  使用flatmap
      .flatMap(new ChannelFreshnessFlatMap)

      /// TODO: 2.分组 按照channelId 和 timeFormat两个字段分组
      .keyBy(line => line.channelId + line.timeFormat)

      /// TODO: 3.划分时间窗口
      .timeWindow(Time.seconds(3))

      /// TODO: 4.聚合
      .reduce(new ChannelFreshnessReduce)

      /// TODO: 5.sink Hbase
      .addSink(new ChannelFreshnessSink)

  }

}
