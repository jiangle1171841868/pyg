package com.itheima.`trait`

import com.itheima.bean.Message
import org.apache.flink.streaming.api.scala.DataStream

/// TODO: 处理任务的接口
trait ProcessData {

  def process(waterData: DataStream[Message])

}
