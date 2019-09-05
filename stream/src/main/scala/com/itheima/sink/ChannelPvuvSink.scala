package com.itheima.sink

import com.itheima.bean.ChannelPvuv
import com.itheima.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

class ChannelPvuvSink extends RichSinkFunction[ChannelPvuv] {


  override def invoke(value: ChannelPvuv): Unit = {

    /**
      * 落地数据： channelID ,time,pv,uv
      * 设计hbase表：
      * 表名：channel
      * 列族：info
      * 列名：channelId ,timeFormat,pv,uv
      * rowkey：channelId +time(格式化的日期)
      */
    val tableName = "channel"
    val family = "info"
    val pvCol = "pv"
    val uvCol = "uv"
    val rowkey = value.getChannelId + value.getTimeFormat

    //获取pvuv数据
    var pv: Long = value.getPv
    var uv: Long = value.getUv

    //先判断hbase的pv uv是否存在 存在就累加
    val pvState: String = HbaseUtil.queryByRowkey(tableName, family, pvCol, rowkey)
    val uvState: String = HbaseUtil.queryByRowkey(tableName, family, uvCol, rowkey)

    //非空检验 进行累加
    if (StringUtils.isNotBlank(pvState)) {

      pv += pvState
    }

    if (StringUtils.isNotBlank(uvState)) {

      uv += uvState
    }

    //封装多列数据，掺入到hbase
    var map= Map[String,Any]()
    map+=(pvCol->pv)
    map+=(uvCol->uv)
    map+=("channelId"->value.getChannelId)
    map+=("time"->value.getTimeFormat)
    HbaseUtil.putMapDataByRowkey(tableName,family,map,rowkey)
  }

}
