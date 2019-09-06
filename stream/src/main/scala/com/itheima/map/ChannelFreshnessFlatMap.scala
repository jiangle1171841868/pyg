package com.itheima.map

import com.itheima.bean.{ChannelFreshness, Message, UserBrowse, UserState}
import com.itheima.util.TimeUtil
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.util.Collector


class ChannelFreshnessFlatMap extends RichFlatMapFunction[Message, ChannelFreshness] {

  //定义日期格式化模板
  val hour = "yyyyMMddHH"
  val day = "yyyyMMdd"
  val month = "yyyyMM"

  override def flatMap(value: Message, out: Collector[ChannelFreshness]): Unit = {

    /// TODO: 1.通过message获取数据
    val userBrowse: UserBrowse = value.userBrowse
    val channelID: Long = userBrowse.channelID
    //获取timestamp userID  调用UserState的方法 获取用户访问状态
    val timestamp: Long = userBrowse.timestamp
    val userID: Long = userBrowse.userID

    val userState: UserState = UserState.getUserState(timestamp, userID)

    /// TODO: 2.获取访问状态
    val isNew: Boolean = userState.isNew
    val firstHour: Boolean = userState.isFirstHour
    val firstDay: Boolean = userState.isFirstDay
    val firstMonth: Boolean = userState.isFirstMonth

    /// TODO: 3.创建ChannelFreshness 封装数据
    val channelFreshness = new ChannelFreshness
    //先封装固定数据
    channelFreshness.setChannelId(channelID)

    /// TODO: 4.格式化数据
    val hourTime: String = TimeUtil.parseTime(timestamp, hour)
    val dayTime: String = TimeUtil.parseTime(timestamp, day)
    val monthTime: String = TimeUtil.parseTime(timestamp, month)

    /// TODO: 5.获取用户状态封装数据
    /**
      * 用户状态是 true: 说明在当前维度内是新用户  newCount=1  并设置当前维度时间
      * 用户状态为 false:说明在当前维度内是老用户  oldCount=1  并设置当前维度时间
      */
    //a.初始化数据
    isNew match {
      case true => channelFreshness.setNewCount(1L)
      case false => channelFreshness.setOldCount(1L)
    }

    /**
      * b.小时维度
      * 1.设置新老用户
      * 2.设置当前维度的格式化时间
      * 3.设置之后要收集 不然会被后面的覆盖
      *
      */
    firstHour match {
      case true =>
        channelFreshness.setNewCount(1L)
        channelFreshness.setTimeFormat(hourTime)
        out.collect(channelFreshness)
      case false =>
        channelFreshness.setOldCount(1L)
        channelFreshness.setTimeFormat(hourTime)
        out.collect(channelFreshness)
    }

    //c.天维度
    firstDay match {
      case true =>
        channelFreshness.setNewCount(1L)
        channelFreshness.setTimeFormat(dayTime)
        out.collect(channelFreshness)
      case false =>
        channelFreshness.setOldCount(1L)
        channelFreshness.setTimeFormat(dayTime)
        out.collect(channelFreshness)
    }

    //d.月维度
    firstMonth match {
      case true =>
        channelFreshness.setNewCount(1L)
        channelFreshness.setTimeFormat(monthTime)
        out.collect(channelFreshness)
      case false =>
        channelFreshness.setOldCount(1L)
        channelFreshness.setTimeFormat(monthTime)
        out.collect(channelFreshness)
    }
  }
}
