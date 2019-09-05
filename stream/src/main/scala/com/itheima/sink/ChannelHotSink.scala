package com.itheima.sink

import com.itheima.bean.ChannelHot
import com.itheima.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class ChannelHotSink extends RichSinkFunction[ChannelHot] {


  //将数据掺入到hbase表
  override def invoke(value: ChannelHot): Unit = {

    val tableName = "channel"
    val family = "info"
    val countCol = "count"
    val rowkey = value.channnelId
    var count: Int = value.count

    //1.查询hbase中是否有channel的countCol数据
    val countData: String = HbaseUtil.queryByRowkey(tableName, family, countCol, rowkey.toString)

    //2.判断countData是否为空  不为就累加数据
    if (StringUtils.isNotBlank(countData)) {
      count += countData.toInt
    }

    //3.将更新后的数据插入到hbase中 两列:channelId和countCol
    val mapData = Map(countCol -> count, "channelId" -> value.channnelId)
    HbaseUtil.putMapDataByRowkey(tableName, family, mapData, rowkey.toString)
  }
}
