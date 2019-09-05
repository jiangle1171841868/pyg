package com.itheima

import java.util.Properties

import com.alibaba.fastjson.{JSON, JSONObject}
import com.itheima.bean.{Message, UserBrowse}
import com.itheima.config.GlobalConfig
import com.itheima.task.ChannelHotTask
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09

/// TODO: 主程序
object AppStream {

  def main(args: Array[String]): Unit = {

    /// TODO: 1.构建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /// TODO: 2.设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    /// TODO: 3.设置检查点
    env.setStateBackend(new FsStateBackend("hdfs://node01:8020/flink/stream/pyg-checkpoint"))
    //周期性触发时间
    env.enableCheckpointing(6000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE) //强一致性
    env.getCheckpointConfig.setCheckpointTimeout(60000) //超时时间：1分钟
    env.getCheckpointConfig.setFailOnCheckpointingErrors(false) //检查点制作失败，任务继续运行
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1) //制作检查点的最大线程
    //任务取消，保留检查点，需要手动删除
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

    /// TODO: 4.获取数据 kafka数据源
    //a.kafka数据源
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", GlobalConfig.kafkaBroker) //broker地址
    properties.setProperty("zookeeper.connect", GlobalConfig.kafkaZk) //zookeeper的地址
    properties.setProperty("group.id", GlobalConfig.kafkaGroupID) //消费组
    val kafkasource = new FlinkKafkaConsumer09[String](GlobalConfig.kafkaTopic, new SimpleStringSchema(), properties)

    //b.加载数据
    val source: DataStream[String] = env.addSource(kafkasource)

    /// TODO: 5.转化数据 封装成case
    val message: DataStream[Message] = source
      .map { data =>
        //a.将json字符串转换为json对象
        val json: JSONObject = JSON.parseObject(data)

        /**
          * b.获取数据封装到样例类
          * 将数据封装到Message中
          * 将message数据封装到UserBrowse中
          */
        val count: Int = json.getIntValue("count")
        val message: String = json.getString("message")
        val timestamp: Long = json.getLong("timestamp")
        val browse: UserBrowse = UserBrowse.parseStr(message)
        Message(count, browse, timestamp)
      }

    /// TODO: 6.获取时间设置水位线
    val waterData: DataStream[Message] = message.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[Message] {

      val delayTime: Long = 2000L
      var currentTimestamp: Long = 0L

      override def getCurrentWatermark: Watermark = {
        new Watermark(currentTimestamp - delayTime)
      }

      //先执行的方法 提取事件 保证时间轴一直往前
      override def extractTimestamp(element: Message, previousElementTimestamp: Long): Long = {

        val timestamp: Long = element.timestamp

        //将最大时间 赋值给currentTimestamp 保证时间轴一直往前
        currentTimestamp = Math.max(currentTimestamp, timestamp)

        //返回任意long类型的数据
        currentTimestamp
      }
    })


    /// TODO: 7.执行任务
    /**
      * 1.实时频道的热点统计
      * 2.实时频道的pvuv分析
      * 3.实时频道的用户新鲜度
      * 4.实时频道的地域分析
      * 5.实时频道的网络类型分析
      * 6.实时频道的浏览器类型分析
      */
    //1.实时频道的热点统计
    ChannelHotTask.process(waterData)


    /// TODO: 8.触发任务
    env.execute()

  }
}
