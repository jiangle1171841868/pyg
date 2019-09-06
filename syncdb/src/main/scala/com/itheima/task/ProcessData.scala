package com.itheima.task


import com.itheima.bean.{Canal, ColumnPair, HbaseOperation}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._

import scala.collection.immutable.StringOps
import scala.collection.mutable.ArrayBuffer

/// TODO: 将数据封装到HbaseOperation中
object ProcessData {

  def process(waterData: DataStream[Canal]) = {

    //获取HbaseOperation中需要的数据
    /**
      * var eventType:String,
      * var tableName:String,
      * var family:String,
      * var columnName:String,
      * var columnValue:String,
      * var rowkey:String
      */

    waterData.flatMap(line => {
      val columnValueList: StringOps = line.columnValueList
      val tableName: String = line.tableName
      val dbName: String = line.dbName
      val eventType: String = line.eventType

      //解析columnValueList 获取的是一行中的列数据的集合
      val pairs: ArrayBuffer[ColumnPair] = ColumnPair.parseData(columnValueList)

      //hbase表名=dbName+tableName
      val hbaseTableName = dbName + "-" + tableName
      val family = "info"
      //将mysql数据库中的主键作为索引   在数组中第一个列就是主键
      val rowkey = pairs(0).columnValue

      //将数据封装到HbaseOperation中
      eventType match {
        case "INSERT" =>
          pairs.map(line => HbaseOperation(eventType, tableName, family, line.columnName, line.columnValue, rowkey))
        case "UPDATE" =>
          //过滤出isValid为true的
          pairs.filter(line => line.isValid).map(line => HbaseOperation(eventType, tableName, family, line.columnName, line.columnValue, rowkey))
        case "DELETE" =>
          //返回值是集合  需要封装到集合 数组中
          List(HbaseOperation(eventType, hbaseTableName, family, null, null, rowkey))
      }
    }

    )

  }
}