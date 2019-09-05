package com.itheima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date 2019/5/30
 */
//@Controller
//方法的返回值是json需要加ResponseBody注解  在类上使用注解RestController 就不用再每个方法上都加上ResponseBody
@Controller
@RequestMapping("report")
public class TestController {

    @RequestMapping("test")
    //RequestBody放在参数列表里面  表示接受json数据
    public void receiveData(String data) {
        System.out.println("data：" + data);
    }
}
