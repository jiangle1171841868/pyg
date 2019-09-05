package com.itheima.map

import com.itheima.bean.{ChannelPvuv, Message, UserBrowse, UserState}
import com.itheima.util.TimeUtil
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.util.Collector

class ChannelPvuvFlatMap extends RichFlatMapFunction[Message, ChannelPvuv] {

  //定义格式化模板
  val hour = "yyyyMMddHH"
  val day = "yyyyMMdd"
  val month = "yyyyMM"

  override def flatMap(value: Message, out: Collector[ChannelPvuv]): Unit = {

    val browse: UserBrowse = value.userBrowse
    val channelID: Long = browse.channelID
    val timestamp: Long = browse.timestamp
    val userID: Long = browse.userID

    //获取用户访问状态
    val userState: UserState = UserState.getUserState(timestamp, userID)
    val isNew: Boolean = userState.isNew
    val firstHour: Boolean = userState.isFirstHour
    val firstDay: Boolean = userState.isFirstDay
    val firstMonth: Boolean = userState.isFirstMonth

    //格式化日期
    val hourTime: String = TimeUtil.parseTime(timestamp, hour)
    val dayTime: String = TimeUtil.parseTime(timestamp, day)
    val monthTime: String = TimeUtil.parseTime(timestamp, month)

    //使用ChannelPvuv封装数据  封装channelId timeFormat pv uv
    val pvuv = new ChannelPvuv
    pvuv.setPv(1L)
    pvuv.setChannelId(channelID)

    //根据用户的时间状态设置值 模式匹配
    //true说明在小时  天 月 是首次访问  uv可以累加 uv设置为1
    //false设置为0
    isNew match {
      case true => pvuv.setPv(1L)
      case false => pvuv.setPv(0L)
    }

    //小时维度
    firstHour match {
      case true =>
        pvuv.setUv(1L)
        //设置时间 是保存到hbase中的列
        pvuv.setTimeFormat(hourTime)
        //更新之后 就收集返回 不会被下面设置的覆盖
        out.collect(pvuv)
      case false =>
        pvuv.setUv(0L)
        pvuv.setTimeFormat(hourTime)
        out.collect(pvuv)
    }

    //天维度
    firstDay match {
      case true =>
        pvuv.setUv(1L)
        pvuv.setTimeFormat(hourTime)
        out.collect(pvuv)
      case false =>
        pvuv.setUv(0L)
        pvuv.setTimeFormat(hourTime)
        out.collect(pvuv)
    }

    //月维度
    firstMonth match {
      case true =>
        pvuv.setUv(1L)
        pvuv.setTimeFormat(monthTime)
        out.collect(pvuv)
      case false =>
        pvuv.setUv(0L)
        pvuv.setTimeFormat(monthTime)
        out.collect(pvuv)
    }
  }
}
