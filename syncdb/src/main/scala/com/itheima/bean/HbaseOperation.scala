package com.itheima.bean

/// TODO: 封装落地hbase的数据
case class HbaseOperation (
                            var eventType:String,
                            var tableName:String,
                            var family:String,
                            var columnName:String,
                            var columnValue:String,
                            var rowkey:String
                          )
