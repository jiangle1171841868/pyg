package com.itheima.producemessage;


import com.alibaba.fastjson.JSONObject;
import com.itheima.bean.UserBrowse;
import com.itheima.test.HttpTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 模拟生产点击流日志消息
 * 调用HttpTest的sendData方法发送数据daocontroler层
 */
public class UserBrowseRecord {
    private static Long[] channelID = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l, 20l};//频道id集合
    private static Long[] categoryID = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l, 20l};//产品类别id集合
    private static Long[] commodityID = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l, 20l};//产品id集合
    private static Long[] userID = new Long[]{1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l, 16l, 17l, 18l, 19l, 20l};//用户id集合

    /**
     * 地区
     */
    private static String[] contrys = new String[]{"China"};//地区-国家集合
    private static String[] provinces = new String[]{"上海"};//地区-省集合
    private static String[] citys = new String[]{"浦东新区", "杨浦区", "虹口区", "徐汇区"};//地区-市集合

    /**
     * 网络方式
     */
    private static String[] networks = new String[]{"电信", "移动", "联通"};

    /**
     * 来源方式
     */
    private static String[] sources = new String[]{"直接输入", "百度跳转", "360搜索跳转", "必应跳转"};

    /**
     * 浏览器
     */
    private static String[] brower = new String[]{"火狐浏览器", "qq浏览器", "360浏览器", "谷歌浏览器", "IE浏览器"};

    /**
     * 打开时间 离开时间
     */
    private static List<Long[]> usetimelog = producetimes();

    //获取时间
    public static List<Long[]> producetimes() {
        List<Long[]> usetimelog = new ArrayList<Long[]>();
        for (int i = 0; i < 100; i++) {
            Long[] timesarray = gettimes("2018-12-12 24:60:60:000");
            usetimelog.add(timesarray);
        }
        return usetimelog;
    }

    private static Long[] gettimes(String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        try {
            Date date = dateFormat.parse(time);

            long timetemp = date.getTime();
            Random random = new Random();
            int randomint = random.nextInt(10);

            long starttime = timetemp - randomint * 3600 * 1000;
            long endtime = starttime + randomint * 3600 * 1000;

            return new Long[]{starttime, endtime};
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Long[]{0l, 0l};
    }

    /**
     * 1.将生成的数据随机封装到UserBrowse (获取的随机索引的方式 获取数据)
     * 2.将UserBrowse转化为json字符串 HttpTest的sendData方法携带数据UserBrowseJson发送到daocontroler层
     */
    public static void main(String[] args) throws Exception {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            //频道id 类别id 产品id 用户id 打开时间 离开时间 地区 网络方式 来源方式 浏览器
            UserBrowse userBrowse = new UserBrowse();
            userBrowse.setChannelID(channelID[random.nextInt(channelID.length)]);
            userBrowse.setCategoryID(categoryID[random.nextInt(categoryID.length)]);
            userBrowse.setProduceID(commodityID[random.nextInt(commodityID.length)]);
            userBrowse.setUserID(userID[random.nextInt(userID.length)]);
            userBrowse.setCountry(contrys[random.nextInt(contrys.length)]);
            userBrowse.setProvince(provinces[random.nextInt(provinces.length)]);
            userBrowse.setCity(citys[random.nextInt(citys.length)]);
            userBrowse.setNetwork(networks[random.nextInt(networks.length)]);
            userBrowse.setSource(sources[random.nextInt(sources.length)]);
            userBrowse.setBrowserType(brower[random.nextInt(brower.length)]);
            userBrowse.setTimestamp(new Date().getTime());

            Long[] times = usetimelog.get(random.nextInt(usetimelog.size()));
            Long tme = times[0];
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
            Date date = new Date(tme);
            String format = dateFormat.format(date);
            System.out.println("<<<<<<<<<<<:" + format);

            userBrowse.setEntryTime(times[0]);
            userBrowse.setLeaveTime(times[1]);

            String jonstr = JSONObject.toJSONString(userBrowse);
            System.out.println(jonstr);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HttpTest.sendData("http://localhost:8090/report/put", jonstr);
        }
    }
}
