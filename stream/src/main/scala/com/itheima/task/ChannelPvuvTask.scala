package com.itheima.task

import com.itheima.`trait`.ProcessData
import com.itheima.bean.Message
import com.itheima.map.ChannelPvuvFlatMap
import com.itheima.reduce.ChannelPvuvReduce
import com.itheima.sink.ChannelPvuvSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object ChannelPvuvTask extends ProcessData {

  override def process(waterData: DataStream[Message]): Unit = {

    //1.数据转换 获取需要的数据 一条数据转换为多条数据 所以用flatmap
    waterData.flatMap(new ChannelPvuvFlatMap)

      //2.分组 对两个字段进行分组
      .keyBy(line => line.channelId+line.timeFormat)

      //3.划分窗口
      .timeWindow(Time.seconds(3))

      //4.聚合
      .reduce(new ChannelPvuvReduce)

      //5.sink Hbase
      .addSink(new ChannelPvuvSink)
  }
}
