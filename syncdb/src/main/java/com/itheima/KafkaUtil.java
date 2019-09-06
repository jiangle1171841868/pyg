package com.itheima;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;


import java.util.Properties;

/**
 * @program: pyg
 * @author: Mr.Jiang
 * @create: 2019-09-05 20:30
 * @description:
 **/
public class KafkaUtil {

    //todo 1.kafka 生产者
    public static Producer getProducer() {

        Properties prop = new Properties();
        prop.put("zookeeper.connect", "node01:2181,node02:2181,node03:2181");
        //broker的地址
        prop.put("metadata.broker.list", "node01:9092,node02:9092,node03:9092");
        prop.put("serializer.class", StringEncoder.class.getName());
        ProducerConfig kafkaConfig = new ProducerConfig(prop);
        Producer<String, String> producer = new Producer<String, String>(kafkaConfig);
        return producer;
    }

    //todo 2.发送信息
    public static void sendData(String topic, String key, String value) {

        Producer producer = getProducer();
        producer.send(new KeyedMessage(topic, key, value));
    }
}
