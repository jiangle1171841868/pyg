package com.itheima.sink

import com.itheima.bean.ChannelFreshness
import com.itheima.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

import scala.collection.mutable

/// TODO: 将数据保存在hdfs上
class ChannelFreshnessSink extends RichSinkFunction[ChannelFreshness] {

  override def invoke(value: ChannelFreshness): Unit = {

    //1.hbase表的基本信息
    /**
      * 数据落地：
      * 设计hbase表：
      * 表名：channel
      * 列族：info
      * 列名：channelId ,time,newCount,oldCount
      * rowkey：channelId +time(格式化的日期)
      **/
    val tableName = "channel"
    val family = "info"
    val newCountCol: String = "newCount"
    val oldCountCol: String = "oldCount"
    val rowkey = value.getChannelId + value.getTimeFormat


    //2.获取新老用户的数量
    var newCount: Long = value.getNewCount
    var oldCount: Long = value.getOldCount

    //3.查询hbase,获取新老用户数据  有数据就累加
    val newCountData: String = HbaseUtil.queryByRowkey(tableName, family, newCountCol, rowkey)
    val oldCountData: String = HbaseUtil.queryByRowkey(tableName, family, oldCountCol, rowkey)

    //4.非空检验 有数据就累加
    if (StringUtils.isNotBlank(newCountData)) {
      newCount += newCountData.toLong
    }
    if (StringUtils.isNoneBlank(oldCountData)) {
      oldCount += oldCountData.toLong
    }

    //5.将数据保存到hbase中
    //将一行  多列数据封装到map集合中 批量插入
    var map = Map[String, Any]()

    map += "channelId" -> value.getChannelId
    map += "time" -> value.timeFormat
    map += "newCountCol" -> newCount
    map += "oldCountCol" -> oldCount

    HbaseUtil.putMapDataByRowkey(tableName, family, map, rowkey)
  }

}
