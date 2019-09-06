package com.itheima.bean

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

import scala.collection.mutable.ArrayBuffer

/// TODO: 封装字段columnValueList中数组 中的元素 就是mysql数据库中的一列数据
case class ColumnPair(
                       var columnName: String,
                       var columnValue: String,
                       //insert的列都是true
                       //update的列是true
                       //delete的列是false
                       var isValid: Boolean
                     )

object ColumnPair {


  def parseData(columnValueList: String) = {

    //解析接送数据为数组  解析出来的是 mysql数据库中的列的数组
    val colums: JSONArray = JSON.parseArray(columnValueList)

    val pairs = new ArrayBuffer[ColumnPair]()

    //遍历 获取每一列的数据 封装到ColumnPair中
    //只能遍历索引  通过索引来获得数组里面的每一个json对象
    for (columIndex <- 0 until colums.size()) {

      //获取每一列数据
      val colum: JSONObject = colums.getJSONObject(columIndex)

      //封装到ColumnPair中
      val pair: ColumnPair = ColumnPair(
        colum.getString("columnName"),
        colum.getString("columnValue"),
        colum.getBoolean("isValid")
      )

      //将数据添加到数组中
      pairs += pair
    }
    pairs
  }
}