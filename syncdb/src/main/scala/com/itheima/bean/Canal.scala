package com.itheima.bean

import com.alibaba.fastjson.{JSON, JSONObject}
import kafka.utils.Json

/// TODO: 封装kafka发送过来的josn数据
case class Canal(
                  var columnValueList: String,
                  var dbName: String,
                  var emptyCount: String,
                  var eventType: String,
                  var logFileName: String,
                  var logFileOffset: String,
                  var tableName: String,
                  var timestamp: String
                )

/// TODO: 创建伴生对象 解析json数据
object Canal {

  def parseJson(jsonStr: String) = {

    //将字符串解析为json对象
    val json: JSONObject = JSON.parseObject(jsonStr)

    //获取json中的数据  封装到Canal样例类中
    Canal(
      json.getString("columnValueList"),
      json.getString("dbName"),
      json.getString("emptyCount"),
      json.getString("eventType"),
      json.getString("logFileName"),
      json.getString("logFileOffset"),
      json.getString("tableName"),
      json.getString("timestamp")
    )
  }


}