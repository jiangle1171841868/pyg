package com.itheima.bean

import scala.beans.BeanProperty

class ChannelPvuv {

  //表示：给bean对象添加get和set方法，类似于javaBean的get和set方法
  @BeanProperty var channelId: Long = 0L
  @BeanProperty var timeFormat:String = null
  @BeanProperty var pv:Long = 0L
  @BeanProperty var uv:Long = 0L

}
