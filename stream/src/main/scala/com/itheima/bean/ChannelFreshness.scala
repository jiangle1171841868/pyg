package com.itheima.bean

import scala.beans.BeanProperty

class ChannelFreshness {

  /// TODO: bean 封装落地到hbase中的数据
  @BeanProperty var channelId: Long = 0L
  @BeanProperty var timeFormat: String = null
  @BeanProperty var newCount: Long = 0L
  @BeanProperty var oldCount: Long = 0L

}
