package com.itheima.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: pyg
 * @author: Mr.Jiang
 * @create: 2019-09-04 20:06
 * @description:
 **/
public class RoundRobinPartition implements Partitioner {

    /**
     * 避免数据倾斜
     * 分区的方法:返回值是几  数据就进去到哪个分区
     * 设置一个自增的变量%分区数(通过Cluster获取) 来决定数据进入到哪个分区
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        //a.创建自增长类 设置初始值为0
        AtomicInteger atomicInteger = new AtomicInteger(0);

        //b.获取分区数
        Integer partitionNum = cluster.partitionCountForTopic(topic);

        //c.自定义分区  atomicInteger.incrementAndGet()每次调用都会自增长1
        int num = atomicInteger.incrementAndGet() % partitionNum;

        //d.当自增长到2000就重置
        if (num >= 2000) {
            atomicInteger.set(0);
        }

        return num;

    }

    public void close() {

    }

    public void configure(Map<String, ?> map) {

    }
}
