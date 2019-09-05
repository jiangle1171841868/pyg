package com.itheima.controller;

import com.alibaba.fastjson.JSON;
import com.itheima.bean.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: pyg
 * @author: Mr.Jiang
 * @create: 2019-09-04 19:51
 * 1.接收模拟url(HttpTest的sendData方法)发送来的UserBrowseJson字符串数据
 * 2.将数据封装到Message的message里面
 * - message属性:
 * - count 消息的条数 默认值1
 * - message 封装UserBrowseJson
 * - timestamp 设置发送的时间
 * 3.发送到kafka
 **/
@RestController //包含@ResponseBody注解 源码中:在他上面使用了@ResponseBody注解
@RequestMapping("report") //映射路径
public class ReportController {

    //todo 1.注入kafkaTemplate
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //todo 2.接收接送数据发送到kafka
    @RequestMapping(value = "put",method = RequestMethod.POST)
    public void putData(@RequestBody String json, HttpServletResponse rsp) throws IOException  {

        //a.封装数据到message
        Message message = new Message();
        message.setAll(1, json, System.currentTimeMillis());

        //b.将数据转化为json字符串 发送到 kafka进行传输
        String messageJson = JSON.toJSONString(message);

        //c.发送
        kafkaTemplate.send("pyg", "test", messageJson);

        //d.发送响应状态给前端用户/或者是访问接口
        PrintWriter printWriter = write(rsp);
        printWriter.flush();
        printWriter.close();
    }


    private PrintWriter write(HttpServletResponse rsp) throws IOException {
        //设置响应头 json
        rsp.setContentType("application/json");
        //设置编码格式
        rsp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = new PrintWriter(rsp.getOutputStream());
        printWriter.write("send success");
        return printWriter;
    }
}
