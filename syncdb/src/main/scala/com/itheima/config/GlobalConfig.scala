package com.itheima.config

import com.typesafe.config.{Config, ConfigFactory}

object GlobalConfig {

  /// TODO: 1.使用 ConfigFactory.load()可以自动加载配置文件中的 application.properties 文件，并返回一个 Config对象
  private val config: Config = ConfigFactory.load()

  /// TODO: 2.获取kafka的配置
  val kafkaBroker: String = config.getString("bootstrap.servers")
  val kafkaZk: String = config.getString("zookeeper.connect")
  val kafkaTopic: String = config.getString("input.topic")
  val kafkaGroupID: String = config.getString("group.id")


  /// TODO: 3.获取hbase的配置
  val hbaseZk: String = config.getString("hbase.zookeeper.quorum")
  val hbaseRpc: String = config.getString("hbase.rpc.timeout")
  val hbaseOperationTimeout: String = config.getString("hbase.client.operation.timeout")
  val hbaseScanTimeout: String = config.getString("hbase.client.scanner.timeout.period")

}
