package com.itheima.bean

import com.itheima.util.{HbaseUtil, TimeUtil}
import org.apache.commons.lang3.StringUtils

//用户状态 首次访问状态值为true 代表uv=1  一个用户每小时 每天 每月第一次访问   设置为true  分别计数
//不是首次访问 状态值为false 代表uv=0
case class UserState(
                      isNew: Boolean = false,
                      isFirstHour: Boolean = false,
                      isFirstDay: Boolean = false,
                      isFirstMonth: Boolean = false
                    )

object UserState {

  //定义格式化模板
  val hour = "yyyyMMddHH"
  val day = "yyyyMMdd"
  val month = "yyyyMM"

  def getUserState(timestamp: Long, userId: Long): UserState = {

    //定义hbase表参数
    val tableName = "userState"
    val family = "info"
    val firstVisitCol = "firstVisitTime" //首次访问时间列
    val lastVisitCol = "lastVisitTime" //最近一次的访问时间列
    val rowkey = userId.toString

    //初始化访问状态
    var isNew: Boolean = false
    var isFirstHour: Boolean = false
    var isFirstDay: Boolean = false
    var isFirstMonth: Boolean = false

    //查询hbase的首次访问时间列的值如果为空 就将状态全部设置为false 并更新首次访问时间和上次最后访问时间
    val firstVisitTime: String = HbaseUtil.queryByRowkey(tableName, family, firstVisitCol, rowkey)

    if (StringUtils.isBlank(firstVisitTime)) {

      //首次访问时间为空  就是首次访问 将状态孩子全部设置为true
      var isNew: Boolean = true
      var isFirstHour: Boolean = true
      var isFirstDay: Boolean = true
      var isFirstMonth: Boolean = true

      //初始化首次访问和最近一次访问的时间
      HbaseUtil.putDataByRowkey(tableName, family, firstVisitCol, firstVisitTime, rowkey)
      HbaseUtil.putDataByRowkey(tableName, family, lastVisitCol, firstVisitTime, rowkey)

      //更新状态
      UserState(isNew, isFirstHour, isFirstDay, isFirstMonth)
    } else {

      //说明不是首次访问  获取最近一次访问时间 和 当前时间进行比较  判断每月 每天 每小时的状态
      //获取最后访问时间  不用非空校验 因为不是第一次访问 就一定有最近一次访问时间
      val lastVisitTime: String = HbaseUtil.queryByRowkey(tableName, family, lastVisitCol, rowkey)

      //指定时间内相同的客户端只被计算一次 超过1个小时 1天 1个月  进行累加计算
      //判断距离最近一次访问时间 是否超过1个小时
      if (TimeUtil.parseTime(timestamp, hour).toLong > TimeUtil.parseTime(lastVisitTime.toLong, hour).toLong) {
        //将小时状态设置为true 可以累加
        isFirstHour = true

      }

      //判断距离最近一次访问时间 是否超过一天
      if (TimeUtil.parseTime(timestamp, day).toLong > TimeUtil.parseTime(lastVisitTime.toLong, day).toLong) {
        //将小时状态设置为true 可以累加
        isFirstDay = true

      }

      //判断距离最近一次访问时间 是否超过一天
      if (TimeUtil.parseTime(timestamp, month).toLong > TimeUtil.parseTime(lastVisitTime.toLong, month).toLong) {
        //将小时状态设置为true 可以累加
        isFirstMonth = true
      }

      //更新最近一次访问时间
      HbaseUtil.putDataByRowkey(tableName, family, lastVisitCol, timestamp.toString, rowkey)

      //封装状态返回
      UserState(isNew, isFirstHour, isFirstDay, isFirstMonth)
    }

  }
}