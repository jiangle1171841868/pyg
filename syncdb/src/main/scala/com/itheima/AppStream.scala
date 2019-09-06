package com.itheima

import java.util.Properties

import com.itheima.bean.{Canal, HbaseOperation}
import com.itheima.config.GlobalConfig
import com.itheima.task.ProcessData
import com.itheima.util.HbaseUtil
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark

object AppStream {

  def main(args: Array[String]): Unit = {

    /// TODO: 1.构建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /// TODO: 2.设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    /// TODO: 3.设置检查点
    env.setStateBackend(new FsStateBackend("hdfs://node01:8020/flink/stream/canal-checkpoint"))
    //a.检查点时间间隔
    env.enableCheckpointing(6000)
    //b.强一致性
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //c.超时时间
    env.getCheckpointConfig.setCheckpointTimeout(60000)
    //d.设置快照制作失败不影响任务
    env.getCheckpointConfig.setFailOnCheckpointingErrors(false)

    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)

    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

    /// TODO: 4.数据源 整合kafka

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", GlobalConfig.kafkaBroker)
    properties.setProperty("zookeeper.connect", GlobalConfig.kafkaZk)
    properties.setProperty("group.id", GlobalConfig.kafkaGroupID)
    val kafkaSource = new FlinkKafkaConsumer09[String](GlobalConfig.kafkaTopic, new SimpleStringSchema(), properties)

    val source: DataStream[String] = env.addSource(kafkaSource)

    /// TODO: 5.数据转化  将json字符串解析 封装到Canal中
    val canals: DataStream[Canal] = source.map(line => Canal.parseJson(line))

    /// TODO: 6.设置水位线
    val waterData: DataStream[Canal] = canals.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[Canal] {

      override def getCurrentWatermark: Watermark = {
        new Watermark(System.currentTimeMillis())
      }

      override def extractTimestamp(element: Canal, previousElementTimestamp: Long): Long = {
        System.currentTimeMillis()
      }
    })

    /// TODO: 7.数据处理
    val value: DataStream[HbaseOperation] = ProcessData.process(waterData)
    value.map(line => {

      line.eventType match {

        case "DELETE" =>
          HbaseUtil.delByRowkey(line.tableName, line.family, line.rowkey)
        case _ =>
          HbaseUtil.putDataByRowkey(line.tableName, line.family, line.columnName, line.columnValue, line.rowkey)
      }
    })

    env.execute()


  }
}