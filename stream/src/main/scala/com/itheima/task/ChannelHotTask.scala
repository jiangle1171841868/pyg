package com.itheima.task

import com.itheima.`trait`.ProcessData
import com.itheima.bean.{ChannelHot, Message}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._
object ChannelHotTask extends ProcessData {

  override def process(waterData: DataStream[Message]): Unit = {

    //1.数据转化
    waterData.map(line => ChannelHot(line.count, line.userBrowse.channelID, line.timestamp))
      //2.按照channelID分组
      .keyBy(line => line.channnelId)
      //3.划分window
      .timeWindow(Time.seconds(3))
      //4.聚合
      .reduce((v1, v2) => ChannelHot(v1.count + v2.count, v1.channnelId, v1.timestmap))
    //5.数据落地hbase表
  }
}
